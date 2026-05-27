package com.kset.common.utils.date;

import java.time.ZoneId;

/**
 * 常见固定偏移时区（按人类习惯的「东 N 区 / 西 N 区」小时偏移定义）。
 * <p>
 * 不含夏令时；有 DST 的地区请用 IANA 时区 ID（如 {@code Asia/Shanghai}）配合 {@link DateZoneHelper#parseZone(String)}。
 */
public enum DateZone {

    /** UTC / GMT 零时区 */
    UTC(0),
    /** 中国（东八区） */
    CN(8),
    /** 沙特阿拉伯（东三区） */
    SAU(3),
    /** 日本（东九区） */
    JP(9),
    /** 新加坡（东八区） */
    SG(8),
    /** 阿联酋（东四区） */
    UAE(4),
    /** 印度（东五区半） */
    IN(5, 30),
    /** 英国（零区，固定偏移） */
    UK(0),
    /** 美东（西五区，固定偏移） */
    US_EAST(-5),
    /** 美西（西八区，固定偏移） */
    US_WEST(-8);

    private final int offsetHours;
    private final int offsetMinutes;

    DateZone(int offsetHours) {
        this(offsetHours, 0);
    }

    DateZone(int offsetHours, int offsetMinutes) {
        this.offsetHours = offsetHours;
        this.offsetMinutes = offsetMinutes;
    }

    /**
     * 相对 UTC 的小时偏移（东为正、西为负）。
     */
    public int getOffsetHours() {
        return offsetHours;
    }

    /**
     * 相对 UTC 的分钟偏移（通常与 {@link #getOffsetHours()} 组合，如印度 +5:30）。
     */
    public int getOffsetMinutes() {
        return offsetMinutes;
    }

    /**
     * 转为 {@link ZoneId}（固定偏移，格式 {@code GMT±H[:mm]}）。
     */
    public ZoneId toZoneId() {
        return DateZoneHelper.zoneOf(offsetHours, offsetMinutes);
    }

    /**
     * 人类可读 GMT 标签，如 {@code GMT+8}、{@code GMT+05:30}。
     */
    public String toGmtLabel() {
        return DateZoneHelper.toGmtLabel(offsetHours, offsetMinutes);
    }

    /**
     * 按整小时偏移查找枚举；未内置则返回 {@code null}。
     */
    public static DateZone ofHours(int offsetHours) {
        for (DateZone zone : values()) {
            if (zone.offsetHours == offsetHours && zone.offsetMinutes == 0) {
                return zone;
            }
        }
        return null;
    }

    /**
     * 解析人类常见时区写法，优先匹配本枚举名（如 {@code CN}、{@code SAU}）。
     */
    public static DateZone parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("zone text must not be blank");
        }
        String s = text.trim();
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            ZoneId zoneId = DateZoneHelper.parseZone(s);
            for (DateZone candidate : values()) {
                if (candidate.toZoneId().equals(zoneId)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("unknown common zone: " + text);
        }
    }
}
