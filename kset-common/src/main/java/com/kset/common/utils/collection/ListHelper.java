package com.kset.common.utils.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 列表工具：分批调用、函数式转换、排序与空安全取用。
 * <p>
 * 日常单元素变换优先使用 JDK {@link java.util.stream.Stream}；
 * IN 查询上限、Redis 分片、批量写库等场景使用本类分批 API。
 * 所有返回 List/Map 的方法均不修改入参，分批结果不使用 {@link List#subList} 视图。
 */
public final class ListHelper {

    private ListHelper() {
    }

    // ── 空安全 ──

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static <T> T firstOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T lastOrNull(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    // ── 分批 ──

    /**
     * 将列表按固定大小拆批，每批为独立 {@link ArrayList}。
     */
    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        if (isEmpty(list)) {
            return List.of();
        }
        requirePositiveBatchSize(batchSize);
        List<List<T>> result = new ArrayList<>((list.size() + batchSize - 1) / batchSize);
        for (int i = 0; i < list.size(); i += batchSize) {
            result.add(copySlice(list, i, Math.min(i + batchSize, list.size())));
        }
        return result;
    }

    /**
     * 按批执行副作用（写库、删缓存等）。
     */
    public static <T> void forEachBatch(List<T> list, int batchSize, Consumer<List<T>> action) {
        Objects.requireNonNull(action, "action");
        if (isEmpty(list)) {
            return;
        }
        requirePositiveBatchSize(batchSize);
        for (int i = 0; i < list.size(); i += batchSize) {
            action.accept(copySlice(list, i, Math.min(i + batchSize, list.size())));
        }
    }

    /**
     * 分批调用后按原顺序拼接结果（如 {@code findByIds} 每 500 条）。
     */
    public static <T, R> List<R> batchMap(List<T> list, int batchSize, Function<List<T>, List<R>> batchFn) {
        Objects.requireNonNull(batchFn, "batchFn");
        if (isEmpty(list)) {
            return List.of();
        }
        requirePositiveBatchSize(batchSize);
        List<R> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            List<T> batch = copySlice(list, i, Math.min(i + batchSize, list.size()));
            List<R> part = batchFn.apply(batch);
            if (part != null && !part.isEmpty()) {
                result.addAll(part);
            }
        }
        return result;
    }

    /**
     * 分批调用后将各批结果 flatten 拼接。
     */
    public static <T, R> List<R> batchFlatMap(List<T> list, int batchSize,
                                              Function<List<T>, Collection<R>> batchFn) {
        Objects.requireNonNull(batchFn, "batchFn");
        if (isEmpty(list)) {
            return List.of();
        }
        requirePositiveBatchSize(batchSize);
        List<R> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            List<T> batch = copySlice(list, i, Math.min(i + batchSize, list.size()));
            Collection<R> part = batchFn.apply(batch);
            if (part != null && !part.isEmpty()) {
                result.addAll(part);
            }
        }
        return result;
    }

    // ── 函数式转换 ──

    public static <T, R> List<R> map(List<T> list, Function<T, R> fn) {
        Objects.requireNonNull(fn, "fn");
        if (isEmpty(list)) {
            return List.of();
        }
        return list.stream().map(fn).collect(Collectors.toList());
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> pred) {
        Objects.requireNonNull(pred, "pred");
        if (isEmpty(list)) {
            return List.of();
        }
        return list.stream().filter(pred).collect(Collectors.toList());
    }

    public static <T, K> Map<K, List<T>> groupBy(List<T> list, Function<T, K> keyFn) {
        Objects.requireNonNull(keyFn, "keyFn");
        if (isEmpty(list)) {
            return Map.of();
        }
        return list.stream().collect(Collectors.groupingBy(keyFn, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 按 key 去重，保留首次出现顺序。
     */
    public static <T, K> List<T> distinctBy(List<T> list, Function<T, K> keyFn) {
        Objects.requireNonNull(keyFn, "keyFn");
        if (isEmpty(list)) {
            return List.of();
        }
        Map<K, T> seen = new LinkedHashMap<>();
        for (T item : list) {
            seen.putIfAbsent(keyFn.apply(item), item);
        }
        return new ArrayList<>(seen.values());
    }

    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyFn, Function<T, V> valueFn) {
        return toMap(list, keyFn, valueFn, null);
    }

    /**
     * 转为 Map；{@code mergeFn == null} 时 key 冲突抛 {@link IllegalStateException}。
     */
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyFn, Function<T, V> valueFn,
                                            BinaryOperator<V> mergeFn) {
        Objects.requireNonNull(keyFn, "keyFn");
        Objects.requireNonNull(valueFn, "valueFn");
        if (isEmpty(list)) {
            return Map.of();
        }
        if (mergeFn == null) {
            return list.stream().collect(Collectors.toMap(
                    keyFn,
                    valueFn,
                    (a, b) -> {
                        throw new IllegalStateException("Duplicate key");
                    },
                    LinkedHashMap::new));
        }
        return list.stream().collect(Collectors.toMap(keyFn, valueFn, mergeFn, LinkedHashMap::new));
    }

    // ── 排序（拷贝后排序，不修改入参）──

    public static <T, U extends Comparable<? super U>> List<T> sortBy(List<T> list, Function<T, U> keyFn) {
        Objects.requireNonNull(keyFn, "keyFn");
        return sort(list, Comparator.comparing(keyFn, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public static <T, U extends Comparable<? super U>> List<T> sortByDesc(List<T> list, Function<T, U> keyFn) {
        Objects.requireNonNull(keyFn, "keyFn");
        return sort(list, Comparator.comparing(keyFn, Comparator.nullsFirst(Comparator.reverseOrder())));
    }

    public static <T> List<T> sort(List<T> list, Comparator<T> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        if (isEmpty(list)) {
            return List.of();
        }
        List<T> copy = new ArrayList<>(list);
        copy.sort(comparator);
        return copy;
    }

    private static void requirePositiveBatchSize(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize 必须大于 0: " + batchSize);
        }
    }

    private static <T> List<T> copySlice(List<T> list, int from, int to) {
        return new ArrayList<>(list.subList(from, to));
    }
}
