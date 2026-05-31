package com.kset.dubbo.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public final class DubboRouteRuleHolder {

    private static final CopyOnWriteArrayList<RouteCondition> CONDITIONS = new CopyOnWriteArrayList<>();
    private static volatile String metadataKey = "version";
    private static volatile String defaultTag = "stable";

    static {
        resetToLocalDefault();
    }

    private DubboRouteRuleHolder() {
    }

    public static void configureLocalDefault(String key, String tag) {
        if (key != null && !key.isBlank()) {
            metadataKey = key;
        }
        if (tag != null && !tag.isBlank()) {
            defaultTag = tag;
        }
        resetToLocalDefault();
    }

    public static String getMetadataKey() {
        return metadataKey;
    }

    public static void update(List<RouteCondition> conditions) {
        CONDITIONS.clear();
        if (conditions == null || conditions.isEmpty()) {
            resetToLocalDefault();
            return;
        }
        conditions.stream()
                .filter(DubboRouteRuleHolder::isValid)
                .forEach(CONDITIONS::add);
        if (CONDITIONS.isEmpty()) {
            resetToLocalDefault();
        }
    }

    public static void resetToLocalDefault() {
        CONDITIONS.clear();
        RouteCondition condition = new RouteCondition();
        condition.setTag(defaultTag);
        condition.setWeight(100);
        CONDITIONS.add(condition);
    }

    public static List<RouteCondition> getConditions() {
        return Collections.unmodifiableList(new ArrayList<>(CONDITIONS));
    }

    public static class RouteCondition {
        private String tag;
        private int weight = 100;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public static class RouteRuleConfig {
        private List<RouteCondition> conditions = new ArrayList<>();

        public List<RouteCondition> getConditions() {
            return conditions;
        }

        public void setConditions(List<RouteCondition> conditions) {
            this.conditions = conditions;
        }
    }

    private static boolean isValid(RouteCondition condition) {
        return condition != null
                && condition.getTag() != null
                && !condition.getTag().isBlank()
                && condition.getWeight() > 0;
    }
}
