package com.kset.common.utils.date;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期时间工具（基于 {@link java.time}，链式 API）。
 * <p>
 * 周期边界命名约定：
 * <ul>
 *   <li>{@code *FirstDate} / {@code dayFirstDate}：周期起点，当天 00:00:00.000</li>
 *   <li>{@code *LastDate}：周期末日，当天 23:59:59.999999999</li>
 *   <li>{@code *EndExclusive}：下一周期起点 00:00:00.000，用于 SQL / 统计左闭右开区间 {@code [start, end)}</li>
 * </ul>
 * 周默认按 ISO：周一为一周起点（{@code weekDay=1}），周日为一周末日。
 */
public class DateHelper {

    public static final String PATTERN_DEF = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DEF_MS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String PATTERN_DAY_DEF = "yyyyMMdd";
    public static final String PATTERN_DAY_SP = "yyyy-MM-dd";
    public static final String PATTERN_MONTH_DEF = "yyyyMM";
    public static final String PATTERN_YEAR_DEF = "yyyy";
    public static final String PATTERN_ONLY_MONTH = "MM";
    public static final String PATTERN_ONLY_DAY = "dd";
    public static final String PATTERN_MM_DD = "MM-dd";
    public static final int MinSec = 60;
    public static final int HouSec = 60 * 60;
    public static final int DaySec = 24 * 60 * 60;
    public static final long SecMil = 1000;
    public static final long MinMil = 60 * 1000;
    public static final long HouMil = 60 * 60 * 1000;
    public static final long DayMil = 24 * 60 * 60 * 1000;
    public static final String CN_GMT = "GMT+8";
    public static final String SAU_GMT = "GMT+3";

    /** ISO 周一，作为默认周起点 */
    public static final int ISO_MONDAY = 1;
    /** ISO 周日，作为默认周终点 */
    public static final int ISO_SUNDAY = 7;

    private static final ZoneId ZONE_CN = ZoneId.of(CN_GMT);
    private static final ZoneId ZONE_SAU = ZoneId.of(SAU_GMT);

    private ZonedDateTime dateTime;
    private ZoneId formatZone;

    private DateHelper(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // ── 构造 ──────────────────────────────────────────────

    public static long nowMil() {
        return System.currentTimeMillis();
    }

    public static long nowSecond() {
        return nowMil() / 1000;
    }

    public static DateHelper build() {
        return new DateHelper(ZonedDateTime.now(ZoneId.systemDefault()));
    }

    public static DateHelper build(ZoneId zone) {
        return new DateHelper(ZonedDateTime.now(zone));
    }

    public static DateHelper build(TimeZone timeZone) {
        return build(timeZone.toZoneId());
    }

    public static DateHelper buildSAU() {
        return build(ZONE_SAU);
    }

    public static DateHelper buildCN() {
        return build(ZONE_CN);
    }

    public static DateHelper of(Date date) {
        return of(date, ZoneId.systemDefault());
    }

    public static DateHelper of(Date date, ZoneId zone) {
        return new DateHelper(date.toInstant().atZone(zone));
    }

    public static DateHelper of(long epochMillis) {
        return of(epochMillis, ZoneId.systemDefault());
    }

    public static DateHelper of(long epochMillis, ZoneId zone) {
        return new DateHelper(Instant.ofEpochMilli(epochMillis).atZone(zone));
    }

    public static DateHelper of(LocalDateTime localDateTime, ZoneId zone) {
        return new DateHelper(localDateTime.atZone(zone));
    }

    public DateHelper copy() {
        return new DateHelper(dateTime);
    }

    // ── 常用周期区间（静态快捷）──────────────────────────

    /** 当前自然月 [月初 00:00, 下月初 00:00) */
    public static DatePeriod thisMonthRange() {
        return build().monthRangeExclusive();
    }

    public static DatePeriod thisMonthRangeCN() {
        return buildCN().monthRangeExclusive();
    }

    /** 上一自然月 */
    public static DatePeriod lastMonthRange() {
        return build().previousMonth().monthRangeExclusive();
    }

    /** 当前 ISO 周 [周一 00:00, 下周一 00:00) */
    public static DatePeriod thisWeekRange() {
        return build().weekRangeExclusive();
    }

    /** 上一 ISO 周 */
    public static DatePeriod lastWeekRange() {
        return build().previousWeek().weekRangeExclusive();
    }

    /** 当前自然年 [年初, 下年初) */
    public static DatePeriod thisYearRange() {
        return build().yearRangeExclusive();
    }

    /** 当天 [00:00, 次日 00:00) */
    public static DatePeriod todayRange() {
        return build().dayRangeExclusive();
    }

    // ── 区间判断 ──────────────────────────────────────────

    /**
     * 判断当前时间是否在 [start, end) 区间内（左闭右开）。
     */
    public boolean isRange(Date start, Date end) {
        long current = toMil();
        return current >= start.getTime() && current < end.getTime();
    }

    /**
     * 判断当前时间是否在 [start, end] 区间内（双闭）。
     */
    public boolean isRangeInclusive(Date start, Date end) {
        long current = toMil();
        return current >= start.getTime() && current <= end.getTime();
    }

    public boolean isBefore(Date other) {
        return toMil() < other.getTime();
    }

    public boolean isAfter(Date other) {
        return toMil() > other.getTime();
    }

    public boolean isSameDay(Date other) {
        return of(other, dateTime.getZone()).toLocalDateTime().toLocalDate()
                .equals(dateTime.toLocalDate());
    }

    public boolean isSameMonth(Date other) {
        LocalDate a = dateTime.toLocalDate();
        LocalDate b = of(other, dateTime.getZone()).toLocalDateTime().toLocalDate();
        return a.getYear() == b.getYear() && a.getMonth() == b.getMonth();
    }

    // ── 赋值 / 解析 ─────────────────────────────────────

    public DateHelper withDate(Date date) {
        ZonedDateTime fromDate = date.toInstant().atZone(dateTime.getZone());
        this.dateTime = dateTime.withYear(fromDate.getYear())
                .withMonth(fromDate.getMonthValue())
                .withDayOfMonth(fromDate.getDayOfMonth())
                .withHour(fromDate.getHour())
                .withMinute(fromDate.getMinute())
                .withSecond(fromDate.getSecond())
                .withNano(fromDate.getNano());
        return this;
    }

    public DateHelper withDate(int year, int month, int day) {
        this.dateTime = dateTime.withYear(year).withMonth(month).withDayOfMonth(day)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    public DateHelper withDate(long epochMillis) {
        this.dateTime = Instant.ofEpochMilli(epochMillis).atZone(dateTime.getZone());
        return this;
    }

    public DateHelper withDate(int year, int month) {
        this.dateTime = dateTime.withYear(year).withMonth(month).withDayOfMonth(1);
        return this;
    }

    public DateHelper withTime(int hour, int minute, int second) {
        this.dateTime = dateTime.withHour(hour).withMinute(minute).withSecond(second).withNano(0);
        return this;
    }

    public DateHelper withTime(int hour, int minute, int second, int nanoOfSecond) {
        this.dateTime = dateTime.withHour(hour).withMinute(minute).withSecond(second).withNano(nanoOfSecond);
        return this;
    }

    // ── 加减 ────────────────────────────────────────────

    public DateHelper addYear(int years) {
        this.dateTime = dateTime.plusYears(years);
        return this;
    }

    public DateHelper addMonth(int month) {
        this.dateTime = dateTime.plusMonths(month);
        return this;
    }

    public DateHelper addWeeks(int weeks) {
        this.dateTime = dateTime.plusWeeks(weeks);
        return this;
    }

    public DateHelper addDay(int day) {
        this.dateTime = dateTime.plusDays(day);
        return this;
    }

    public DateHelper addHour(int hour) {
        this.dateTime = dateTime.plusHours(hour);
        return this;
    }

    public DateHelper addMinutes(int minutes) {
        this.dateTime = dateTime.plusMinutes(minutes);
        return this;
    }

    public DateHelper addSecond(int second) {
        this.dateTime = dateTime.plusSeconds(second);
        return this;
    }

    /** 上个月（保持日、时分秒，月底会自动收敛） */
    public DateHelper previousMonth() {
        return addMonth(-1);
    }

    /** 下个月 */
    public DateHelper nextMonth() {
        return addMonth(1);
    }

    /** 上一周（减 7 天） */
    public DateHelper previousWeek() {
        return addWeeks(-1);
    }

    /** 下一周 */
    public DateHelper nextWeek() {
        return addWeeks(1);
    }

    /** 上一年 */
    public DateHelper previousYear() {
        return addYear(-1);
    }

    /** 下一年 */
    public DateHelper nextYear() {
        return addYear(1);
    }

    /** 跳到上一周期首日：先对齐到当月/当周/当年首日，再减一周期 */
    public DateHelper firstDayOfPreviousMonth() {
        return monthFirstDate().addMonth(-1);
    }

    public DateHelper firstDayOfNextMonth() {
        return monthFirstDate().addMonth(1);
    }

    public DateHelper firstDayOfPreviousWeek() {
        return weekFirstDate().addWeeks(-1);
    }

    public DateHelper firstDayOfPreviousYear() {
        return yearFirstDate().addYear(-1);
    }

    public DateHelper withDateStr(String time, String pattern) {
        LocalDateTime parsed = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
        this.dateTime = parsed.atZone(dateTime.getZone());
        return this;
    }

    public DateHelper withDateDef(String time) {
        return withDateStr(time, PATTERN_DEF);
    }

    public DateHelper withDateDay(String time) {
        return withDateStr(time, PATTERN_DAY_DEF);
    }

    public DateHelper withDateDaySP(String time) {
        return withDateStr(time, PATTERN_DAY_SP);
    }

    public DateHelper withDateMonth(String yyyyMM) {
        LocalDateTime parsed = LocalDateTime.parse(yyyyMM + "01",
                DateTimeFormatter.ofPattern(PATTERN_DAY_DEF));
        this.dateTime = parsed.withHour(0).withMinute(0).withSecond(0).withNano(0)
                .atZone(dateTime.getZone());
        return this;
    }

    // ── 日边界 ──────────────────────────────────────────

    /** 当天 00:00:00.000 */
    public DateHelper dayFirstDate() {
        this.dateTime = dateTime.toLocalDate().atStartOfDay(dateTime.getZone());
        return this;
    }

    /** 当天 23:59:59.999999999 */
    public DateHelper dayLastDate() {
        this.dateTime = dateTime.toLocalDate().atTime(LocalTime.MAX).atZone(dateTime.getZone());
        return this;
    }

    /** 次日 00:00:00.000，作为当天统计右开边界 */
    public DateHelper dayEndExclusive() {
        this.dateTime = dateTime.toLocalDate().plusDays(1).atStartOfDay(dateTime.getZone());
        return this;
    }

    public DatePeriod dayRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().dayFirstDate().toDate(),
                copy().dayEndExclusive().toDate());
    }

    // ── 周边界（默认 ISO 周一至周日）────────────────────

    public DateHelper weekFirstDate() {
        return weekFirstDate(ISO_MONDAY);
    }

    /**
     * @param weekDay ISO 星期：1=周一 … 7=周日，表示一周的起点
     */
    public DateHelper weekFirstDate(int weekDay) {
        DayOfWeek target = DayOfWeek.of(weekDay);
        LocalDate weekStart = dateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(target));
        this.dateTime = weekStart.atStartOfDay(dateTime.getZone());
        return this;
    }

    /** 当前周末日 23:59:59.999999999（默认周日） */
    public DateHelper weekLastDate() {
        return weekLastDate(ISO_MONDAY);
    }

    /**
     * @param weekStartDay 一周起点（ISO 1-7），周末为起点 +6 天
     */
    public DateHelper weekLastDate(int weekStartDay) {
        LocalDate weekEnd = dateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.of(weekStartDay)))
                .plusDays(6);
        this.dateTime = weekEnd.atTime(LocalTime.MAX).atZone(dateTime.getZone());
        return this;
    }

    /** 下周一 00:00:00.000（默认周一起算） */
    public DateHelper weekEndExclusive() {
        return weekEndExclusive(ISO_MONDAY);
    }

    public DateHelper weekEndExclusive(int weekStartDay) {
        LocalDate nextWeekStart = dateTime.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.of(weekStartDay)))
                .plusWeeks(1);
        this.dateTime = nextWeekStart.atStartOfDay(dateTime.getZone());
        return this;
    }

    public DatePeriod weekRangeExclusive() {
        return weekRangeExclusive(ISO_MONDAY);
    }

    public DatePeriod weekRangeExclusive(int weekStartDay) {
        return DatePeriod.ofExclusiveEnd(
                copy().weekFirstDate(weekStartDay).toDate(),
                copy().weekEndExclusive(weekStartDay).toDate());
    }

    // ── 月边界 ──────────────────────────────────────────

    /** 当月 1 日 00:00:00.000 */
    public DateHelper monthFirstDate() {
        this.dateTime = dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /** 当月最后一日 23:59:59.999999999 */
    public DateHelper monthLastDate() {
        LocalDate lastDay = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        this.dateTime = lastDay.atTime(LocalTime.MAX).atZone(dateTime.getZone());
        return this;
    }

    /** 下月 1 日 00:00:00.000 */
    public DateHelper monthEndExclusive() {
        LocalDate firstNext = dateTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextMonth());
        this.dateTime = firstNext.atStartOfDay(dateTime.getZone());
        return this;
    }

    public DatePeriod monthRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().monthFirstDate().toDate(),
                copy().monthEndExclusive().toDate());
    }

    // ── 季边界 ──────────────────────────────────────────

    /** 当前季度首日 00:00:00.000（Q1=1月, Q2=4月 …） */
    public DateHelper quarterFirstDate() {
        int month = dateTime.getMonthValue();
        int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
        this.dateTime = dateTime.withMonth(quarterStartMonth).withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /** 当前季度末日 23:59:59.999999999 */
    public DateHelper quarterLastDate() {
        int month = dateTime.getMonthValue();
        int quarterEndMonth = ((month - 1) / 3) * 3 + 3;
        LocalDate lastDay = LocalDate.of(dateTime.getYear(), quarterEndMonth, 1)
                .with(TemporalAdjusters.lastDayOfMonth());
        this.dateTime = lastDay.atTime(LocalTime.MAX).atZone(dateTime.getZone());
        return this;
    }

    /** 下季度首日 00:00:00.000 */
    public DateHelper quarterEndExclusive() {
        int month = dateTime.getMonthValue();
        int quarterStartMonth = ((month - 1) / 3) * 3 + 1;
        LocalDate nextQuarterStart = LocalDate.of(dateTime.getYear(), quarterStartMonth, 1).plusMonths(3);
        this.dateTime = nextQuarterStart.atStartOfDay(dateTime.getZone());
        return this;
    }

    public DatePeriod quarterRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().quarterFirstDate().toDate(),
                copy().quarterEndExclusive().toDate());
    }

    // ── 年边界 ──────────────────────────────────────────

    /** 当年 1 月 1 日 00:00:00.000 */
    public DateHelper yearFirstDate() {
        this.dateTime = dateTime.with(TemporalAdjusters.firstDayOfYear())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return this;
    }

    /** 当年 12 月 31 日 23:59:59.999999999 */
    public DateHelper yearLastDate() {
        LocalDate lastDay = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfYear());
        this.dateTime = lastDay.atTime(LocalTime.MAX).atZone(dateTime.getZone());
        return this;
    }

    /** 下年 1 月 1 日 00:00:00.000 */
    public DateHelper yearEndExclusive() {
        LocalDate firstNext = dateTime.toLocalDate().with(TemporalAdjusters.firstDayOfNextYear());
        this.dateTime = firstNext.atStartOfDay(dateTime.getZone());
        return this;
    }

    public DatePeriod yearRangeExclusive() {
        return DatePeriod.ofExclusiveEnd(
                copy().yearFirstDate().toDate(),
                copy().yearEndExclusive().toDate());
    }

    // ── 间隔计算 ────────────────────────────────────────

    /** 与另一时刻相差的天数（按本地日历日，可负） */
    public long daysUntil(Date other) {
        LocalDate a = dateTime.toLocalDate();
        LocalDate b = of(other, dateTime.getZone()).toLocalDateTime().toLocalDate();
        return ChronoUnit.DAYS.between(a, b);
    }

    public long daysBetween(Date other) {
        return Math.abs(daysUntil(other));
    }

    public long hoursBetween(Date other) {
        return Math.abs(ChronoUnit.HOURS.between(dateTime.toInstant(), of(other, dateTime.getZone()).toZonedDateTime().toInstant()));
    }

    public long minutesBetween(Date other) {
        return Math.abs(ChronoUnit.MINUTES.between(dateTime, of(other, dateTime.getZone()).toZonedDateTime()));
    }

    // ── 转换 / 格式化 ───────────────────────────────────

    public Date toDate() {
        return Date.from(dateTime.toInstant());
    }

    public LocalDateTime toLocalDateTime() {
        return dateTime.toLocalDateTime();
    }

    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    public ZonedDateTime toZonedDateTime() {
        return dateTime;
    }

    public DateHelper formatZone(TimeZone zone) {
        this.formatZone = zone.toZoneId();
        return this;
    }

    public DateHelper formatZone(ZoneId zone) {
        this.formatZone = zone;
        return this;
    }

    public String format(String pattern) {
        ZoneId zone = formatZone != null ? formatZone : dateTime.getZone();
        return dateTime.withZoneSameInstant(zone).format(DateTimeFormatter.ofPattern(pattern));
    }

    public long toMil() {
        return dateTime.toInstant().toEpochMilli();
    }

    public int toSecond() {
        return (int) (toMil() / 1000);
    }

    public String toyyyyMMddHHmmss() {
        return format(PATTERN_DEF);
    }

    public String toMM() {
        return format(PATTERN_ONLY_MONTH);
    }

    public String toDD() {
        return format(PATTERN_ONLY_DAY);
    }

    public String toyyyyMMddHHmmssSSS() {
        return format(PATTERN_DEF_MS);
    }

    public String toyyyyMMdd() {
        return format(PATTERN_DAY_DEF);
    }

    public String toyyyyMMddT() {
        return format(PATTERN_DAY_SP);
    }

    public String toyyyyMM() {
        return format(PATTERN_MONTH_DEF);
    }

    public String toMMDD() {
        return format(PATTERN_MM_DD);
    }

    public String toyyyy() {
        return format(PATTERN_YEAR_DEF);
    }

    /**
     * 每 5 分钟唯一 key，格式 yyyyMMddHHmm_5，例如 202406071405_5
     */
    public String uniqueKeyPer5Min() {
        int minute = dateTime.getMinute();
        int min5 = (minute / 5) * 5;
        String key = dateTime.withMinute(min5).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return key + "_5";
    }

    /**
     * 每 1 分钟唯一 key，格式 yyyyMMddHHmm_1，例如 202406071409_1
     */
    public String uniqueKeyPer1Min() {
        String key = dateTime.withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return key + "_1";
    }

    /**
     * 左闭右开时间区间 {@code [startInclusive, endExclusive)}，适用于 SQL、报表统计。
     */
    public static final class DatePeriod {
        private final Date startInclusive;
        private final Date endExclusive;

        private DatePeriod(Date startInclusive, Date endExclusive) {
            this.startInclusive = startInclusive;
            this.endExclusive = endExclusive;
        }

        public static DatePeriod ofExclusiveEnd(Date startInclusive, Date endExclusive) {
            return new DatePeriod(startInclusive, endExclusive);
        }

        public Date getStartInclusive() {
            return startInclusive;
        }

        public Date getEndExclusive() {
            return endExclusive;
        }

        public boolean contains(Date instant) {
            long t = instant.getTime();
            return t >= startInclusive.getTime() && t < endExclusive.getTime();
        }

        public boolean containsNow() {
            return contains(new Date());
        }

        @Override
        public String toString() {
            return "[" + startInclusive + ", " + endExclusive + ")";
        }
    }
}