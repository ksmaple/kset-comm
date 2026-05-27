package com.kset.common.utils.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListHelperTest {

    @Test
    void shouldPartitionWithRemainder() {
        List<List<Integer>> batches = ListHelper.partition(List.of(1, 2, 3, 4, 5), 2);
        assertEquals(3, batches.size());
        assertEquals(List.of(1, 2), batches.get(0));
        assertEquals(List.of(3, 4), batches.get(1));
        assertEquals(List.of(5), batches.get(2));
        assertTrue(batches.get(0) instanceof ArrayList);
    }

    @Test
    void shouldReturnEmptyForNullOrEmptyPartition() {
        assertEquals(List.of(), ListHelper.partition(null, 10));
        assertEquals(List.of(), ListHelper.partition(List.of(), 10));
    }

    @Test
    void shouldRejectInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> ListHelper.partition(List.of(1), 0));
        assertThrows(IllegalArgumentException.class, () -> ListHelper.forEachBatch(List.of(1), -1, b -> {
        }));
    }

    @Test
    void shouldBatchMapInOrder() {
        List<Integer> ids = List.of(1, 2, 3, 4, 5);
        List<String> result = ListHelper.batchMap(ids, 2, batch ->
                ListHelper.map(batch, id -> "id-" + id));
        assertEquals(List.of("id-1", "id-2", "id-3", "id-4", "id-5"), result);
    }

    @Test
    void shouldForEachBatchWithoutSubListView() {
        List<Integer> source = new ArrayList<>(List.of(1, 2, 3));
        AtomicInteger count = new AtomicInteger();
        ListHelper.forEachBatch(source, 2, batch -> {
            count.addAndGet(batch.size());
            batch.clear();
        });
        assertEquals(3, count.get());
        assertEquals(List.of(1, 2, 3), source);
    }

    @Test
    void shouldDistinctByKeepFirstOrder() {
        record Item(String key, int seq) {
        }
        List<Item> list = List.of(
                new Item("a", 1),
                new Item("b", 2),
                new Item("a", 3),
                new Item("c", 4));
        List<Item> distinct = ListHelper.distinctBy(list, Item::key);
        assertEquals(3, distinct.size());
        assertEquals(1, distinct.get(0).seq());
        assertEquals(2, distinct.get(1).seq());
        assertEquals(4, distinct.get(2).seq());
    }

    @Test
    void shouldSortByWithNullsLast() {
        record Row(Integer score) {
        }
        List<Row> sorted = ListHelper.sortBy(
                List.of(new Row(3), new Row(null), new Row(1)),
                Row::score);
        assertEquals(1, sorted.get(0).score());
        assertEquals(3, sorted.get(1).score());
        assertNull(sorted.get(2).score());
    }

    @Test
    void shouldSortByDescWithNullsFirst() {
        record Row(Integer score) {
        }
        List<Row> sorted = ListHelper.sortByDesc(
                List.of(new Row(3), new Row(null), new Row(1)),
                Row::score);
        assertNull(sorted.get(0).score());
        assertEquals(3, sorted.get(1).score());
        assertEquals(1, sorted.get(2).score());
    }

    @Test
    void shouldGroupByPreserveOrder() {
        record Item(String type, int id) {
        }
        List<Item> list = List.of(new Item("a", 1), new Item("b", 2), new Item("a", 3));
        Map<String, List<Item>> grouped = ListHelper.groupBy(list, Item::type);
        assertEquals(List.of("a", "b"), List.copyOf(grouped.keySet()));
        assertEquals(2, grouped.get("a").size());
    }

    @Test
    void shouldHandleNullAsEmptyForMapAndFilter() {
        assertEquals(List.of(), ListHelper.map(null, Object::toString));
        assertEquals(List.of(), ListHelper.filter(null, x -> true));
        assertTrue(ListHelper.isEmpty(null));
        assertNull(ListHelper.firstOrNull(null));
    }

    @Test
    void shouldThrowOnDuplicateKeyWithoutMerge() {
        record Item(int id, String name) {
        }
        List<Item> list = List.of(new Item(1, "a"), new Item(1, "b"));
        assertThrows(IllegalStateException.class,
                () -> ListHelper.toMap(list, Item::id, Item::name));
    }
}
