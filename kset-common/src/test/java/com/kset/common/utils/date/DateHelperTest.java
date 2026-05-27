package com.kset.common.utils.date;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateHelperTest {

    @Test
    void shouldFormatWithJavaTime() {
        String formatted = DateHelper.parse("2024-06-07 14:05:30").toyyyyMMddHHmmss();
        assertEquals("2024-06-07 14:05:30", formatted);
    }

    @Test
    void shouldParseHumanFormats() {
        assertEquals("2024-06-07 14:05:30", DateHelper.parse("2024-06-07 14:05:30").toyyyyMMddHHmmss());
        assertEquals("2024-06-07 00:00:00", DateHelper.parse("2024-06-07").toyyyyMMddHHmmss());
        assertEquals("2024-06-07 00:00:00", DateHelper.parse("20240607").toyyyyMMddHHmmss());
        assertEquals("2024-06-01 00:00:00", DateHelper.parse("202406").toyyyyMMddHHmmss());
        assertEquals("2024-01-01 00:00:00", DateHelper.parse("2024").toyyyyMMddHHmmss());
        assertEquals("2024-06-07 15:30:00", DateHelper.parse("2024-06-07").withTime("15:30:00").toyyyyMMddHHmmss());
        assertEquals("2024-06-07 14:05:30", DateHelper.build().at("2024-06-07 14:05:30").toyyyyMMddHHmmss());
    }

    @Test
    void shouldCheckRange() {
        Date start = DateHelper.parse("2024-01-01 00:00:00").toDate();
        Date end = DateHelper.parse("2024-01-02 00:00:00").toDate();
        assertTrue(DateHelper.parse("2024-01-01 12:00:00").isRange(start, end));
        assertTrue(DateHelper.parse("2024-01-02 00:00:00").isRange(start, end));
        assertFalse(DateHelper.parse("2024-01-02 00:00:01").isRange(start, end));
        assertFalse(DateHelper.parse("2024-01-02 00:00:00").isRangeExclusive(start, end));
    }

    @Test
    void shouldCheckRangeBoundary() {
        Date start = DateHelper.parse("2024-06-01 00:00:00").toDate();
        Date end = DateHelper.parse("2024-06-30 23:59:59").toDate();
        Date atStart = start;
        Date atEnd = end;
        Date afterEnd = DateHelper.parse("2024-07-01 00:00:00").toDate();

        assertTrue(DateHelper.isInRange(atStart, start, end));
        assertTrue(DateHelper.isInRange(atEnd, start, end));
        assertFalse(DateHelper.isInRange(afterEnd, start, end));
        assertTrue(DateHelper.isInRangeInclusive(atEnd, start, end));
    }

    @Test
    void shouldGenerateUniqueKeys() {
        assertEquals("202406071400_5",
                DateHelper.parse("2024-06-07 14:03:00").uniqueKeyPer5Min());
        assertEquals("202406071403_1",
                DateHelper.parse("2024-06-07 14:03:45").uniqueKeyPer1Min());
    }

    @Test
    void shouldComputeMonthBoundaries() {
        DateHelper mid = DateHelper.parse("2024-06-15 10:30:00");

        assertEquals("2024-06-01 00:00:00", mid.copy().monthFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-30 23:59:59", mid.copy().monthLastDate().toyyyyMMddHHmmss());
        assertEquals("2024-07-01 00:00:00", mid.copy().monthEndExclusive().toyyyyMMddHHmmss());

        DateHelper.DatePeriod exclusive = mid.monthRangeExclusive();
        assertEquals("2024-06-01 00:00:00",
                DateHelper.of(exclusive.getStartInclusive()).toyyyyMMddHHmmss());
        assertEquals("2024-07-01 00:00:00",
                DateHelper.of(exclusive.getEndExclusive()).toyyyyMMddHHmmss());
        assertTrue(exclusive.isExclusiveEnd());
        assertTrue(exclusive.contains(DateHelper.parse("2024-06-30 23:59:59").toDate()));
        assertFalse(exclusive.contains(DateHelper.parse("2024-07-01 00:00:00").toDate()));

        DateHelper.DatePeriod inclusive = mid.monthRangeInclusive();
        assertEquals("2024-06-30 23:59:59",
                DateHelper.of(inclusive.getEndInclusive()).toyyyyMMddHHmmss());
        assertFalse(inclusive.isExclusiveEnd());
        assertTrue(inclusive.contains(DateHelper.parse("2024-06-30 23:59:59").toDate()));
        assertFalse(inclusive.contains(DateHelper.parse("2024-07-01 00:00:00").toDate()));
    }

    @Test
    void shouldComputeWeekBoundaries() {
        // 2024-06-07 周五
        DateHelper fri = DateHelper.parse("2024-06-07 15:00:00");

        assertEquals("2024-06-03 00:00:00", fri.copy().weekFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-09 23:59:59", fri.copy().weekLastDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-10 00:00:00", fri.copy().weekEndExclusive().toyyyyMMddHHmmss());

        DateHelper.DatePeriod week = fri.weekRangeExclusive();
        assertEquals(7, DateHelper.of(week.getStartInclusive())
                .daysUntil(week.getEndExclusive()));
    }

    @Test
    void shouldComputeYearAndQuarterBoundaries() {
        DateHelper d = DateHelper.parse("2024-08-20 12:00:00");

        assertEquals("2024-01-01 00:00:00", d.copy().yearFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-12-31 23:59:59", d.copy().yearLastDate().toyyyyMMddHHmmss());
        assertEquals("2025-01-01 00:00:00", d.copy().yearEndExclusive().toyyyyMMddHHmmss());

        assertEquals("2024-07-01 00:00:00", d.copy().quarterFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-09-30 23:59:59", d.copy().quarterLastDate().toyyyyMMddHHmmss());
        assertEquals("2024-10-01 00:00:00", d.copy().quarterEndExclusive().toyyyyMMddHHmmss());
    }

    @Test
    void shouldNavigatePreviousPeriods() {
        assertEquals("2024-05-01 00:00:00",
                DateHelper.parse("2024-06-15 00:00:00")
                        .firstDayOfPreviousMonth().toyyyyMMddHHmmss());
        assertEquals("2024-05-27 00:00:00",
                DateHelper.parse("2024-06-07 00:00:00")
                        .firstDayOfPreviousWeek().toyyyyMMddHHmmss());
    }

    @Test
    void shouldComputeDaySpan() {
        Date a = DateHelper.parse("2024-06-01 00:00:00").toDate();
        Date b = DateHelper.parse("2024-06-08 00:00:00").toDate();
        assertEquals(7, DateHelper.of(a).daysUntil(b));
        assertEquals(7, DateHelper.of(a).daysBetween(b));
    }

    @Test
    void shouldCompareSameDayAndMonth() {
        DateHelper a = DateHelper.parse("2024-06-07 08:00:00");
        Date morning = DateHelper.parse("2024-06-07 01:00:00").toDate();
        Date otherMonth = DateHelper.parse("2024-07-07 01:00:00").toDate();

        assertTrue(a.isSameDay(morning));
        assertTrue(a.isSameMonth(morning));
        assertFalse(a.isSameMonth(otherMonth));
    }

    @Test
    void shouldComputeStaticDaySpan() {
        Date a = DateHelper.parse("2024-06-01 00:00:00").toDate();
        Date b = DateHelper.parse("2024-06-08 00:00:00").toDate();
        assertEquals(7, DateHelper.daysUntil(a, b));
        assertEquals(7, DateHelper.daysBetween(a, b));
    }

    @Test
    void shouldComputeStaticSecondsBetween() {
        Date a = DateHelper.parse("2024-06-01 00:00:00").toDate();
        Date b = DateHelper.parse("2024-06-01 00:01:30").toDate();
        assertEquals(90, DateHelper.secondsBetween(a, b));
    }

    @Test
    void shouldBuildCustomRanges() {
        Date start = DateHelper.parse("2024-06-01 00:00:00").toDate();
        Date end = DateHelper.parse("2024-06-30 23:59:59").toDate();
        Date next = DateHelper.parse("2024-07-01 00:00:00").toDate();

        DateHelper.DatePeriod inclusive = DateHelper.rangeInclusive(start, end);
        assertTrue(inclusive.contains(end));
        assertFalse(inclusive.contains(next));

        DateHelper.DatePeriod exclusive = DateHelper.rangeExclusive(start, next);
        assertTrue(exclusive.contains(end));
        assertFalse(exclusive.contains(next));
    }

    @Test
    void shouldPickMinMax() {
        Date earlier = DateHelper.parse("2024-01-01 00:00:00").toDate();
        Date later = DateHelper.parse("2024-06-01 00:00:00").toDate();
        assertEquals(earlier, DateHelper.min(earlier, later));
        assertEquals(later, DateHelper.max(earlier, later));
    }
}
