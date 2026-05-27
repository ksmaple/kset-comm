package com.kset.common.utils.date;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具（基于 {@link java.time}，链式 API）。
 * <p>
 * 仅维护<strong>服务器本地</strong>墙钟时间（{@link LocalDateTime}），含常见时间计算；
 * 跨时区转换见 {@link DateZoneHelper}。
 * <p>
 * 周期边界命名约定：
 * <ul>
 *   <li>{@code *FirstDate} / {@code dayFirstDate}：周期起点，当天 00:00:00.000</li>
 *   <li>{@code *LastDate}：周期末日，当天 23:59:59.999999999</li>
 *   <li>{@code *EndExclusive}：下一周期起点 00:00:00.000，用于 SQL / 统计左闭右开区间 {@code [start, end)}</li>
 * </ul>
 * 时间对比与 {@link DatePeriod} 均支持两种边界：
 * <ul>
 *   <li>左闭右闭 {@code [start, end]}：{@link #isInRange}、{@link DatePeriod#ofInclusiveEnd}</li>
 *   <li>左闭右开 {@code [start, end)}：{@link #isInRangeExclusive}、{@link DatePeriod#ofExclusiveEnd}（SQL / 统计）</li>
 * </ul>
 * 周默认按 ISO：周一为一周起点（{@code weekDay=1}），周日为一周末日。
 * <p>
 * 创建时间优先使用 {@link #parse(String)} / {@link #of(String)}，自动识别
 * {@code yyyy-MM-dd HH:mm:ss}、{@code yyyy-MM-dd}、{@code yyyyMMdd}、{@code yyyyMM}、{@code yyyy} 等常见写法。
 */
public class DateHelper {

    /** 默认日期时间格式：{@code yyyy-MM-dd HH:mm:ss} */
    public static final String PATTERN_DEF = "yyyy-MM-dd HH:mm:ss";
    /** 带毫秒日期时间格式：{@code yyyy-MM-dd HH:mm:ss:SSS} */
    public static final String PATTERN_DEF_MS = "yyyy-MM-dd HH:mm:ss:SSS";
    /** 紧凑日期格式：{@code yyyyMMdd} */
    public static final String PATTERN_DAY_DEF = "yyyyMMdd";
    /** 标准日期格式：{@code yyyy-MM-dd} */
    public static final String PATTERN_DAY_SP = "yyyy-MM-dd";
    /** 紧凑年月格式：{@code yyyyMM} */
    public static final String PATTERN_MONTH_DEF = "yyyyMM";
    /** 年份格式：{@code yyyy} */
    public static final String PATTERN_YEAR_DEF = "yyyy";
    /** 月份格式：{@code MM} */
    public static final String PATTERN_ONLY_MONTH = "MM";
    /** 日格式：{@code dd} */
    public static final String PATTERN_ONLY_DAY = "dd";
    /** 月日格式：{@code MM-dd} */
    public static final String PATTERN_MM_DD = "MM-dd";
    /** 一分钟秒数 */
    public static final int MinSec = 60;
    /** 一小时秒数 */
    public static final int HouSec = 60 * 60;
    /** 一天秒数 */
    public static final int DaySec = 24 * 60 * 60;
    /** 一秒毫秒数 */
    public static final long SecMil = 1000;
    /** 一分钟毫秒数 */
    public static final long MinMil = 60 * 1000;
    /** 一小时毫秒数 */
    public static final long HouMil = 60 * 60 * 1000;
    /** 一天毫秒数 */
    public static final long DayMil = 24 * 60 * 60 * 1000;

    /** ISO 周一，作为默认周起点 */
    public static final int ISO_MONDAY = 1;
    /** ISO 周日，作为默认周终点 */
    public static final int ISO_SUNDAY = 7;

    private LocalDateTime dateTime;

    private DateHelper(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // ── 构造 ──────────────────────────────────────────────

    /**
     * 当前 epoch 毫秒时间戳。
     */
    public static long nowMil() {
        return System.currentTimeMillis();
    }

    /**
     * 当前 epoch 秒时间戳。
     */
    public static long nowSecond() {
        return nowMil() / 1000;
    }

    /**
     * 以服务器本地当前时刻构造实例。
     */
    public static DateHelper build() {
        return new DateHelper(LocalDateTime.now());
    }

    /**
     * 当前时刻，同 {@link #build()}。
     */
    public static DateHelper now() {
        return build();
    }

    /**
     * 智能解析人类常见日期时间字符串（自动识别格式）。
     * <p>
     * 支持：{@code yyyy-MM-dd HH:mm:ss}、{@code yyyy-MM-dd HH:mm:ss:SSS}、
     * {@code yyyy-MM-dd}、{@code yyyyMMdd}、{@code yyyyMM}、{@code yyyy}。
     */
    public static DateHelper parse(String text) {
        return new DateHelper(parseToLocalDateTime(text));
    }

    /**
     * 解析字符串构造，同 {@link #parse(String)}。
     */
    public static DateHelper of(String text) {
        return parse(text);
    }

    /**
     * 由 {@link Date} 构造（按服务器本地时区解读墙钟时间）。
     */
    public static DateHelper of(Date date) {
        return new DateHelper(toLocalDateTime(date));
    }

    /**
     * 由 epoch 毫秒构造（按服务器本地时区解读墙钟时间）。
     */
    public static DateHelper of(long epochMillis) {
        return new DateHelper(toLocalDateTime(epochMillis));
    }

    /**
     * 由 {@link LocalDateTime} 构造。
     */
    public static DateHelper of(LocalDateTime localDateTime) {
        return new DateHelper(localDateTime);
    }

    /**
     * 由 {@link LocalDate} 构造，时分秒为 00:00:00。
     */
    public static DateHelper of(LocalDate localDate) {
        return new DateHelper(localDate.atStartOfDay());
    }

    /**
     * 复制当前墙钟时间，用于链式运算中避免修改原实例。
     */
    public DateHelper copy() {
        return new DateHelper(dateTime);
    }

    // ── 常用周期区间（静态快捷）──────────────────────────

    /**
     * 当前自然月 {@code [月初 00:00, 下月初 00:00)}（左闭右开，SQL / 统计）。
     */
    public static DatePeriod thisMonthRange() {
        return build().monthRangeExclusive();
    }

    /**
     * 当前自然月 {@code [月初 00:00, 月末 23:59:59.999…]}（左闭右闭）。
     */
    public static DatePeriod thisMonthRangeInclusive() {
        return build().monthRangeInclusive();
    }

    /**
     * 上一自然月 {@code [月初, 下月初)}（左闭右开）。
     */
    public static DatePeriod lastMonthRange() {
        return build().previousMonth().monthRangeExclusive();
    }

    /**
     * 上一自然月 {@code [月初, 月末]}（左闭右闭）。
     */
    public static DatePeriod lastMonthRangeInclusive() {
        return build().previousMonth().monthRangeInclusive();
    }

    /**
     * 当前 ISO 周 {@code [周一 00:00, 下周一 00:00)}（左闭右开）。
     */
    public static DatePeriod thisWeekRange() {
        return build().weekRangeExclusive();
    }

    /**
     * 当前 ISO 周 {@code [周一 00:00, 周日 23:59:59.999…]}（左闭右闭）。
     */
    public static DatePeriod thisWeekRangeInclusive() {
        return build().weekRangeInclusive();
    }

    /**
     * 上一 ISO 周 {@code [周一, 下周一)}（左闭右开）。
     */
    public static DatePeriod lastWeekRange() {
        return build().previousWeek().weekRangeExclusive();
    }

    /**
     * 上一 ISO 周 {@code [周一, 周日]}（左闭右闭）。
     */
    public static DatePeriod lastWeekRangeInclusive() {
        return build().previousWeek().weekRangeInclusive();
    }

    /**
     * 当前自然年 {@code [年初, 下年初)}（左闭右开）。
     */
    public static DatePeriod thisYearRange() {
        return build().yearRangeExclusive();
    }

    /**
     * 当前自然年 {@code [年初, 年末]}（左闭右闭）。
     */
    public static DatePeriod thisYearRangeInclusive() {
        return build().yearRangeInclusive();
    }

    /**
     * 当天 {@code [00:00, 次日 00:00)}（左闭右开）。
     */
    public static DatePeriod todayRange() {
        return build().dayRangeExclusive();
    }

    /**
     * 当天 {@code [00:00, 23:59:59.999…]}（左闭右闭）。
     */
    public static DatePeriod todayRangeInclusive() {
        return build().dayRangeInclusive();
    }

    /**
     * 构造左闭右闭区间 {@code [start, end]}。
     */
    public static DatePeriod rangeInclusive(Date start, Date end) {
        return DatePeriod.ofInclusiveEnd(start, end);
    }

    /**
     * 构造左闭右开区间 {@code [start, end)}。
     */
    public static DatePeriod rangeExclusive(Date start, Date endExclusive) {
        return DatePeriod.ofExclusiveEnd(start, endExclusive);
    }

    // ── 静态时间计算 ──────────────────────────────────────

    /**
     * 两时刻相差天数（按本地日历日）。
     *
     * @param start 起点
     * @param end   终点；早于 {@code start} 时结果为负
     */
    public static long daysUntil(Date start, Date end) {
        return ChronoUnit.DAYS.between(toLocalDate(start), toLocalDate(end));
    }

    /**
     * 两时刻相差天数绝对值（按本地日历日）。
     */
    public static long daysBetween(Date a, Date b) {
        return Math.abs(daysUntil(a, b));
    }

    /**
     * 两时刻相差小时数绝对值。
     */
    public static long hoursBetween(Date a, Date b) {
        return Math.abs(ChronoUnit.HOURS.between(toLocalDateTime(a), toLocalDateTime(b)));
    }

    /**
     * 两时刻相差分钟数绝对值。
     */
    public static long minutesBetween(Date a, Date b) {
        return Math.abs(ChronoUnit.MINUTES.between(toLocalDateTime(a), toLocalDateTime(b)));
    }

    /**
     * 两时刻相差秒数绝对值。
     */
    public static long secondsBetween(Date a, Date b) {
        return Math.abs(ChronoUnit.SECONDS.between(toLocalDateTime(a), toLocalDateTime(b)));
    }

    /**
     * {@code a} 是否早于 {@code b}。
     */
    public static boolean isBefore(Date a, Date b) {
        return a.getTime() < b.getTime();
    }

    /**
     * {@code a} 是否晚于 {@code b}。
     */
    public static boolean isAfter(Date a, Date b) {
        return a.getTime() > b.getTime();
    }

    /**
     * 两时刻是否同一本地日历日。
     */
    public static boolean isSameDay(Date a, Date b) {
        return toLocalDate(a).equals(toLocalDate(b));
    }

    /**
     * 两时刻是否同一本地自然月。
     */
    public static boolean isSameMonth(Date a, Date b) {
        LocalDate da = toLocalDate(a);
        LocalDate db = toLocalDate(b);
        return da.getYear() == db.getYear() && da.getMonth() == db.getMonth();
    }

    /**
     * {@code point} 是否为今天（本地日历日）。
     */
    public static boolean isToday(Date point) {
        return isSameDay(point, new Date());
    }

    /**
     * {@code point} 是否在 {@code [start, end]} 内（左闭右闭）。
     */
    public static boolean isInRange(Date point, Date start, Date end) {
        long t = point.getTime();
        return t >= start.getTime() && t <= end.getTime();
    }

    /**
     * {@code point} 是否在 {@code [start, end)} 内（左闭右开，适用于 SQL / 统计区间）。
     */
    public static boolean isInRangeExclusive(Date point, Date start, Date endExclusive) {
        long t = point.getTime();
        return t >= start.getTime() && t < endExclusive.getTime();
    }

    /**
     * {@code point} 是否在 {@code [start, end]} 内（左闭右闭，同 {@link #isInRange}）。
     */
    public static boolean isInRangeInclusive(Date point, Date start, Date endInclusive) {
        return isInRange(point, start, endInclusive);
    }

    /**
     * 返回较晚的时刻。
     */
    public static Date max(Date a, Date b) {
        return isAfter(a, b) ? a : b;
    }

    /**
     * 返回较早的时刻。
     */
    public static Date min(Date a, Date b) {
        return isBefore(a, b) ? a : b;
    }

    // ── 区间判断（实例）──────────────────────────────────

    /**
     * 当前时刻是否在 {@code [start, end]} 内（左闭右闭）。
     */
    public boolean isRange(Date start, Date end) {
        return isInRange(toDate(), start, end);
    }

    /**
     * 当前时刻是否在 {@code [start, end)} 内（左闭右开）。
     */
    public boolean isRangeExclusive(Date start, Date endExclusive) {
        return isInRangeExclusive(toDate(), start, endExclusive);
    }

    /**
     * 当前时刻是否在 {@code [start, end]} 内（左闭右闭，同 {@link #isRange}）。
     */
    public boolean isRangeInclusive(Date start, Date end) {
        return isInRange(toDate(), start, end);
    }

    /**
     * 当前时刻是否早于 {@code other}。
     */
    public boolean isBefore(Date other) {
        return DateHelper.isBefore(toDate(), other);
    }

    /**
     * 当前时刻是否晚于 {@code other}。
     */
    public boolean isAfter(Date other) {
        return DateHelper.isAfter(toDate(), other);
    }

    /**
     * 当前时刻与 {@code other} 是否同一本地日历日。
     */
    public boolean isSameDay(Date other) {
        return DateHelper.isSameDay(toDate(), other);
    }

    /**
     * 当前时刻与 {@code other} 是否同一本地自然月。
     */
    public boolean isSameMonth(Date other) {
        return DateHelper.isSameMonth(toDate(), other);
    }

    // ── 赋值 / 解析 ─────────────────────────────────────

    /**
     * 智能解析并重设本地墙钟时间（格式同 {@link #parse(String)}）。
     */
    public DateHelper at(String text) {
        this.dateTime = parseToLocalDateTime(text);
        return this;
    }

    /**
     * 将年月日时分秒替换为 {@code date} 在本地时区下的墙钟分量，保留链式调用。
     */
    public DateHelper withDate(Date date) {
        LocalDateTime fromDate = toLocalDateTime(date);
        this.dateTime = dateTime.withYear(fromDate.getYear())
                .withMonth(fromDate.getMonthValue())
                .withDayOfMonth(fromDate.getDayOfMonth())
                .withHour(fromDate.getHour())
                .withMinute(fromDate.getMinute())
                .withSecond(fromDate.getSecond())
                .withNano(fromDate.getNano());
        return this;
    }

    /**
     * 设置为 epoch 毫秒对应的本地墙钟时间。
     */
    public DateHelper withDate(long epochMillis) {
        this.dateTime = toLocalDateTime(epochMillis);
        return this;
    }

    /**
     * 设置当天本地时分秒，格式 {@code HH:mm:ss} 或 {@code HH:mm:ss.SSS}。
     */
    public DateHelper withTime(String time) {
        LocalTime parsed = LocalTime.parse(time, time.contains(".")
                ? DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                : DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.dateTime = dateTime.withHour(parsed.getHour())
                .withMinute(parsed.getMinute())
                .withSecond(parsed.getSecond())
                .withNano(parsed.getNano());
        return this;
    }

    /**
     * 按自定义格式解析字符串并设为本地墙钟时间。
     */
    public DateHelper withPattern(String text, String pattern) {
        this.dateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        return this;
    }

    // ── 加减 ────────────────────────────────────────────

    /**
     * 增加 {@code years} 年。
     */
    public DateHelper addYear(int years) {
        this.dateTime = dateTime.plusYears(years);
        return this;
    }

    /**
     * 增加 {@code month} 月（负数为减）。
     */
    public DateHelper addMonth(int month) {
        this.dateTime = dateTime.plusMonths(month);
        return this;
    }

    /**
     * 增加 {@code weeks} 周（负数为减）。
     */
    public DateHelper addWeeks(int weeks) {
        this.dateTime = dateTime.plusWeeks(weeks);
        return this;
    }

    /**
     * 增加 {@code day} 天（负数为减）。
     */
    public DateHelper addDay(int day) {
        this.dateTime = dateTime.plusDays(day);
        return this;
    }

    /**
     * 增加 {@code hour} 小时（负数为减）。
     */
    public DateHelper addHour(int hour) {
        this.dateTime = dateTime.plusHours(hour);
        return this;
    }

    /**
     * 增加 {@code minutes} 分钟（负数为减）。
     */
    public DateHelper addMinutes(int minutes) {
        this.dateTime = dateTime.plusMinutes(minutes);
        return this;
    }

    /**
     * 增加 {@code second} 秒（负数为减）。
     */
    public DateHelper addSecond(int second) {
        this.dateTime = dateTime.plusSeconds(second);
        return this;
    }

    /**
     * 减 1 月（保持日、时分秒，月底会自动收敛）。
     */
    public DateHelper previousMonth() {
        return addMonth(-1);
    }

    /**
     * 加 1 月。
     */
    public DateHelper nextMonth() {
        return addMonth(1);
    }

    /**
     * 减 1 周（7 天）。
     */
    public DateHelper previousWeek() {
        return addWeeks(-1);
    }

    /**
     * 加 1 周。
     */
    public DateHelper nextWeek() {
        return addWeeks(1);
    }

    /**
     * 减 1 年。
     */
    public DateHelper previousYear() {
        return addYear(-1);
    }

    /**
     * 加 1 年。
     */
    public DateHelper nextYear() {
        return addYear(1);
    }

    /**
     * 对齐到当月 1 日后再减 1 月，时分秒归零。
     */
    public DateHelper firstDayOfPreviousMonth() {
        return monthFirstDate().addMonth(-1);
    }

    /**
     * 对齐到当月 1 日后再加 1 月，时分秒归零。
     */
    public DateHelper firstDayOfNextMonth() {
        return monthFirstDate().addMonth(1);
    }

    /**
     * 对齐到当周首日后减 1 周，时分秒归零。
     */
    public DateHelper firstDayOfPreviousWeek() {
        return weekFirstDate().addWeeks(-1);
    }

    /**
     * 对齐到当年 1 月 1 日后再减 1 年，时分秒归零。
     */
    public DateHelper firstDayOfPreviousYear() {
        return yearFirstDate().addYear(-1);
    }

    // ── 日边界 ──────────────────────────────────────────

    /**
     * 对齐到当天 00:00:00.000。
     */
    public DateHelper dayFirstDate() {
        this.dateTime = dateTime.toLocalDate().atStartOfDay();
        return this;
    }

    /**
     * 对齐到当天 23:59:59.999999999。
     */
    public DateHelper dayLastDate() {
        this.dateTime = dateTime.toLocalDate().atTime(LocalTime.MAX);
        return this;
    }

    /**
     * 对齐到次日 00:00:00.000，作为当天统计右开边界。
     */
    public DateHelper dayEndExclusive() {
        this.dateTime = dateTime.toLocalDate().plusDays(1).atStartOfDay();
        return this;
    }

    /**
     * 当天区间 {@code [00:00, 次日 00:00)}（左闭右开）。
     */
    public DatePeriod dayRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().dayFirstDate().toDate(),
                copy().dayEndExclusive().toDate());
    }

    /**
     * 当天区间 {@code [00:00, 23:59:59.999…]}（左闭右闭）。
     */
    public DatePeriod dayRangeInclusive() {
        return DatePeriod.ofInclusiveEnd(
                copy().dayFirstDate().toDate(),
                copy().dayLastDate().toDate());
    }

    // ── 周边界（默认 ISO 周一至周日）────────────────────

    /**
     * 对齐到当前 ISO 周周一 00:00:00.000。
     */
    public DateHelper weekFirstDate() {
        return weekFirstDate(ISO_MONDAY);
    }

    /**
     * 对齐到以 {@code weekDay} 为起点的当周首日 00:00:00.000。
     *
     * @param weekDay ISO 星期：1=周一 … 7=周日
     */
    public DateHelper weekFirstDate(int weekDay) {
        DayOfWeek target = DayOfWeek.of(weekDay);
        this.dateTime = dateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(target)).atStartOfDay();
        return this;
    }

    /**
     * 对齐到当前 ISO 周周日 23:59:59.999999999。
     */
    public DateHelper weekLastDate() {
        return weekLastDate(ISO_MONDAY);
    }

    /**
     * 对齐到以 {@code weekStartDay} 为起点的当周末日 23:59:59.999999999。
     *
     * @param weekStartDay 一周起点（ISO 1-7），周末为起点 +6 天
     */
    public DateHelper weekLastDate(int weekStartDay) {
        LocalDate weekEnd = dateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.of(weekStartDay)))
                .plusDays(6);
        this.dateTime = weekEnd.atTime(LocalTime.MAX);
        return this;
    }

    /**
     * 对齐到下周一 00:00:00.000（默认周一起算）。
     */
    public DateHelper weekEndExclusive() {
        return weekEndExclusive(ISO_MONDAY);
    }

    /**
     * 对齐到下一周期起点 00:00:00.000。
     *
     * @param weekStartDay 一周起点（ISO 1-7）
     */
    public DateHelper weekEndExclusive(int weekStartDay) {
        LocalDate nextWeekStart = dateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.of(weekStartDay)))
                .plusWeeks(1);
        this.dateTime = nextWeekStart.atStartOfDay();
        return this;
    }

    /**
     * 当前 ISO 周区间 {@code [周一 00:00, 下周一 00:00)}（左闭右开）。
     */
    public DatePeriod weekRangeExclusive() {
        return weekRangeExclusive(ISO_MONDAY);
    }

    /**
     * 以 {@code weekStartDay} 为起点的当周区间 {@code [首日起, 下周期首日起)}（左闭右开）。
     */
    public DatePeriod weekRangeExclusive(int weekStartDay) {
        return DatePeriod.ofExclusiveEnd(
                copy().weekFirstDate(weekStartDay).toDate(),
                copy().weekEndExclusive(weekStartDay).toDate());
    }

    /**
     * 当前 ISO 周区间 {@code [周一 00:00, 周日 23:59:59.999…]}（左闭右闭）。
     */
    public DatePeriod weekRangeInclusive() {
        return weekRangeInclusive(ISO_MONDAY);
    }

    /**
     * 以 {@code weekStartDay} 为起点的当周区间 {@code [首日起, 末日的]}（左闭右闭）。
     */
    public DatePeriod weekRangeInclusive(int weekStartDay) {
        return DatePeriod.ofInclusiveEnd(
                copy().weekFirstDate(weekStartDay).toDate(),
                copy().weekLastDate(weekStartDay).toDate());
    }

    // ── 月边界 ──────────────────────────────────────────

    /**
     * 对齐到当月 1 日 00:00:00.000。
     */
    public DateHelper monthFirstDate() {
        this.dateTime = dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /**
     * 对齐到当月最后一日 23:59:59.999999999。
     */
    public DateHelper monthLastDate() {
        LocalDate lastDay = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        this.dateTime = lastDay.atTime(LocalTime.MAX);
        return this;
    }

    /**
     * 对齐到下月 1 日 00:00:00.000。
     */
    public DateHelper monthEndExclusive() {
        LocalDate firstNext = dateTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextMonth());
        this.dateTime = firstNext.atStartOfDay();
        return this;
    }

    /**
     * 当月区间 {@code [1 日 00:00, 下月 1 日 00:00)}（左闭右开）。
     */
    public DatePeriod monthRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().monthFirstDate().toDate(),
                copy().monthEndExclusive().toDate());
    }

    /**
     * 当月区间 {@code [1 日 00:00, 月末 23:59:59.999…]}（左闭右闭）。
     */
    public DatePeriod monthRangeInclusive() {
        return DatePeriod.ofInclusiveEnd(
                copy().monthFirstDate().toDate(),
                copy().monthLastDate().toDate());
    }

    // ── 季边界 ──────────────────────────────────────────

    /**
     * 对齐到当前季度首日 00:00:00.000（Q1=1 月, Q2=4 月 …）。
     */
    public DateHelper quarterFirstDate() {
        int month = dateTime.getMonthValue();
        int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
        this.dateTime = dateTime.withMonth(quarterStartMonth).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /**
     * 对齐到当前季度末日 23:59:59.999999999。
     */
    public DateHelper quarterLastDate() {
        int month = dateTime.getMonthValue();
        int quarterEndMonth = ((month - 1) / 3) * 3 + 3;
        LocalDate lastDay = LocalDate.of(dateTime.getYear(), quarterEndMonth, 1)
                .with(TemporalAdjusters.lastDayOfMonth());
        this.dateTime = lastDay.atTime(LocalTime.MAX);
        return this;
    }

    /**
     * 对齐到下季度首日 00:00:00.000。
     */
    public DateHelper quarterEndExclusive() {
        int month = dateTime.getMonthValue();
        int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
        LocalDate nextQuarterStart = LocalDate.of(dateTime.getYear(), quarterStartMonth, 1).plusMonths(3);
        this.dateTime = nextQuarterStart.atStartOfDay();
        return this;
    }

    /**
     * 当前季度区间 {@code [季首 00:00, 下季首 00:00)}（左闭右开）。
     */
    public DatePeriod quarterRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().quarterFirstDate().toDate(),
                copy().quarterEndExclusive().toDate());
    }

    /**
     * 当前季度区间 {@code [季首 00:00, 季末 23:59:59.999…]}（左闭右闭）。
     */
    public DatePeriod quarterRangeInclusive() {
        return DatePeriod.ofInclusiveEnd(
                copy().quarterFirstDate().toDate(),
                copy().quarterLastDate().toDate());
    }

    // ── 年边界 ──────────────────────────────────────────

    /**
     * 对齐到当年 1 月 1 日 00:00:00.000。
     */
    public DateHelper yearFirstDate() {
        this.dateTime = dateTime.with(TemporalAdjusters.firstDayOfYear())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /**
     * 对齐到当年 12 月 31 日 23:59:59.999999999。
     */
    public DateHelper yearLastDate() {
        LocalDate lastDay = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfYear());
        this.dateTime = lastDay.atTime(LocalTime.MAX);
        return this;
    }

    /**
     * 对齐到下年 1 月 1 日 00:00:00.000。
     */
    public DateHelper yearEndExclusive() {
        LocalDate firstNext = dateTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextYear());
        this.dateTime = firstNext.atStartOfDay();
        return this;
    }

    /**
     * 当年区间 {@code [1 月 1 日 00:00, 下年 1 月 1 日 00:00)}（左闭右开）。
     */
    public DatePeriod yearRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().yearFirstDate().toDate(),
                copy().yearEndExclusive().toDate());
    }

    /**
     * 当年区间 {@code [1 月 1 日 00:00, 12 月 31 日 23:59:59.999…]}（左闭右闭）。
     */
    public DatePeriod yearRangeInclusive() {
        return DatePeriod.ofInclusiveEnd(
                copy().yearFirstDate().toDate(),
                copy().yearLastDate().toDate());
    }

    // ── 间隔计算（实例）──────────────────────────────────

    /**
     * 与 {@code other} 相差天数（按本地日历日，可负）。
     */
    public long daysUntil(Date other) {
        return DateHelper.daysUntil(toDate(), other);
    }

    /**
     * 与 {@code other} 相差天数绝对值。
     */
    public long daysBetween(Date other) {
        return DateHelper.daysBetween(toDate(), other);
    }

    /**
     * 与 {@code other} 相差小时数绝对值。
     */
    public long hoursBetween(Date other) {
        return DateHelper.hoursBetween(toDate(), other);
    }

    /**
     * 与 {@code other} 相差分钟数绝对值。
     */
    public long minutesBetween(Date other) {
        return DateHelper.minutesBetween(toDate(), other);
    }

    /**
     * 与 {@code other} 相差秒数绝对值。
     */
    public long secondsBetween(Date other) {
        return DateHelper.secondsBetween(toDate(), other);
    }

    // ── 转换 / 格式化 ───────────────────────────────────

    /**
     * 转为 {@link Date}（同一时刻，按服务器本地时区）。
     */
    public Date toDate() {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 转为 {@link LocalDateTime}。
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }

    /**
     * 转为 {@link LocalDate}。
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * 按自定义格式格式化为字符串。
     */
    public String format(String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 转为 epoch 毫秒时间戳。
     */
    public long toMil() {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 转为 epoch 秒时间戳。
     */
    public int toSecond() {
        return (int) (toMil() / 1000);
    }

    /**
     * 格式化为 {@link #PATTERN_DEF}。
     */
    public String toyyyyMMddHHmmss() {
        return format(PATTERN_DEF);
    }

    /**
     * 格式化为 {@link #PATTERN_ONLY_MONTH}。
     */
    public String toMM() {
        return format(PATTERN_ONLY_MONTH);
    }

    /**
     * 格式化为 {@link #PATTERN_ONLY_DAY}。
     */
    public String toDD() {
        return format(PATTERN_ONLY_DAY);
    }

    /**
     * 格式化为 {@link #PATTERN_DEF_MS}。
     */
    public String toyyyyMMddHHmmssSSS() {
        return format(PATTERN_DEF_MS);
    }

    /**
     * 格式化为 {@link #PATTERN_DAY_DEF}。
     */
    public String toyyyyMMdd() {
        return format(PATTERN_DAY_DEF);
    }

    /**
     * 格式化为 {@link #PATTERN_DAY_SP}。
     */
    public String toyyyyMMddT() {
        return format(PATTERN_DAY_SP);
    }

    /**
     * 格式化为 {@link #PATTERN_MONTH_DEF}。
     */
    public String toyyyyMM() {
        return format(PATTERN_MONTH_DEF);
    }

    /**
     * 格式化为 {@link #PATTERN_MM_DD}。
     */
    public String toMMDD() {
        return format(PATTERN_MM_DD);
    }

    /**
     * 格式化为 {@link #PATTERN_YEAR_DEF}。
     */
    public String toyyyy() {
        return format(PATTERN_YEAR_DEF);
    }

    /**
     * 每 5 分钟唯一 key，格式 {@code yyyyMMddHHmm_5}，例如 {@code 202406071405_5}。
     */
    public String uniqueKeyPer5Min() {
        int minute = dateTime.getMinute();
        int min5 = (minute / 5) * 5;
        String key = dateTime.withMinute(min5).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return key + "_5";
    }

    /**
     * 每 1 分钟唯一 key，格式 {@code yyyyMMddHHmm_1}，例如 {@code 202406071409_1}。
     */
    public String uniqueKeyPer1Min() {
        String key = dateTime.withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return key + "_1";
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static LocalDateTime toLocalDateTime(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * 解析人类常见日期时间字符串为 {@link LocalDateTime}。
     */
    private static LocalDateTime parseToLocalDateTime(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("date text must not be blank");
        }
        String s = text.trim();

        if (s.length() >= 23 && s.charAt(4) == '-' && s.charAt(7) == '-' && s.charAt(10) == ' '
                && s.charAt(19) == ':') {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(PATTERN_DEF_MS));
        }
        if (s.length() >= 19 && s.charAt(4) == '-' && s.charAt(7) == '-' && s.charAt(10) == ' ') {
            return LocalDateTime.parse(s.substring(0, 19), DateTimeFormatter.ofPattern(PATTERN_DEF));
        }
        if (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }
        if (s.length() == 8 && isDigits(s)) {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern(PATTERN_DAY_DEF)).atStartOfDay();
        }
        if (s.length() == 6 && isDigits(s)) {
            return LocalDate.parse(s + "01", DateTimeFormatter.ofPattern(PATTERN_DAY_DEF)).atStartOfDay();
        }
        if (s.length() == 4 && isDigits(s)) {
            return LocalDate.parse(s + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }

        throw new DateTimeParseException("unsupported date text: " + text, text, 0);
    }

    private static boolean isDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 时间区间，支持左闭右闭 {@code [start, end]} 与左闭右开 {@code [start, end)} 两种边界。
     */
    public static final class DatePeriod {

        /** 区间边界类型 */
        public enum BoundaryType {
            /** 左闭右闭 {@code [start, end]} */
            INCLUSIVE_END,
            /** 左闭右开 {@code [start, end)}，适用于 SQL / 统计 */
            EXCLUSIVE_END
        }

        private final Date startInclusive;
        private final Date end;
        private final BoundaryType boundaryType;

        private DatePeriod(Date startInclusive, Date end, BoundaryType boundaryType) {
            this.startInclusive = startInclusive;
            this.end = end;
            this.boundaryType = boundaryType;
        }

        /**
         * 构造左闭右闭区间 {@code [startInclusive, endInclusive]}。
         */
        public static DatePeriod ofInclusiveEnd(Date startInclusive, Date endInclusive) {
            return new DatePeriod(startInclusive, endInclusive, BoundaryType.INCLUSIVE_END);
        }

        /**
         * 构造左闭右开区间 {@code [startInclusive, endExclusive)}。
         *
         * @param endExclusive 终点（不含）
         */
        public static DatePeriod ofExclusiveEnd(Date startInclusive, Date endExclusive) {
            return new DatePeriod(startInclusive, endExclusive, BoundaryType.EXCLUSIVE_END);
        }

        /**
         * 区间边界类型。
         */
        public BoundaryType getBoundaryType() {
            return boundaryType;
        }

        /**
         * 是否为左闭右开区间。
         */
        public boolean isExclusiveEnd() {
            return boundaryType == BoundaryType.EXCLUSIVE_END;
        }

        /**
         * 区间起点（含）。
         */
        public Date getStartInclusive() {
            return startInclusive;
        }

        /**
         * 区间终点（左闭右闭时含，左闭右开时不含）。
         */
        public Date getEnd() {
            return end;
        }

        /**
         * 左闭右闭区间的终点（含）；左闭右开时与 {@link #getEndExclusive()} 语义不同，调用前请确认 {@link #isExclusiveEnd()}。
         */
        public Date getEndInclusive() {
            if (boundaryType != BoundaryType.INCLUSIVE_END) {
                throw new IllegalStateException("not an inclusive-end period, use getEndExclusive()");
            }
            return end;
        }

        /**
         * 左闭右开区间的终点（不含）；左闭右闭时与 {@link #getEndInclusive()} 语义不同，调用前请确认 {@link #isExclusiveEnd()}。
         */
        public Date getEndExclusive() {
            if (boundaryType != BoundaryType.EXCLUSIVE_END) {
                throw new IllegalStateException("not an exclusive-end period, use getEndInclusive()");
            }
            return end;
        }

        /**
         * 判断 {@code instant} 是否在区间内（按构造时的边界类型）。
         */
        public boolean contains(Date instant) {
            return boundaryType == BoundaryType.INCLUSIVE_END
                    ? DateHelper.isInRange(instant, startInclusive, end)
                    : DateHelper.isInRangeExclusive(instant, startInclusive, end);
        }

        /**
         * 当前时刻是否在区间内。
         */
        public boolean containsNow() {
            return contains(new Date());
        }

        @Override
        public String toString() {
            return boundaryType == BoundaryType.INCLUSIVE_END
                    ? "[" + startInclusive + ", " + end + "]"
                    : "[" + startInclusive + ", " + end + ")";
        }
    }
}
