package com.kset.cache.core;

import java.io.Serializable;

public class KsetCacheValue implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean nullValue;
    private Object value;

    public KsetCacheValue() {
    }

    private KsetCacheValue(boolean nullValue, Object value) {
        this.nullValue = nullValue;
        this.value = value;
    }

    public static KsetCacheValue of(Object value) {
        return value == null ? nullValue() : new KsetCacheValue(false, value);
    }

    public static KsetCacheValue nullValue() {
        return new KsetCacheValue(true, null);
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object unwrap() {
        return nullValue ? null : value;
    }
}
