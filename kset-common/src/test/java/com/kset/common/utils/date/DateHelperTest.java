package com.kset.common.utils.date;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateHelperTest {

    @Test
    void shouldFormatWithJavaTime() {
        String formatted = DateHelper.build()
                .withDate(2024, 6, 7)
                .withDateDef("2024-06-07 14:05:30")
                .toyyyyMMddHHmmss();
        assertEquals("2024-06-07 14:05:30", formatted);
    }

    @Test
    void shouldBuildWithZone() {
        assertEquals(ZoneId.of(DateHelper.SAU_GMT),
                DateHelper.buildSAU().toZonedDateTime().getZone());
    }

    @Test
    void shouldCheckRange() {
        Date start = DateHelper.build().withDateDef("2024-01-01 00:00:00").toDate();
        Date end = DateHelper.build().withDateDef("2024-01-02 00:00:00").toDate();
        assertTrue(DateHelper.build().withDateDef("2024-01-01 12:00:00").isRange(start, end));
        assertFalse(DateHelper.build().withDateDef("2024-01-02 00:00:00").isRange(start, end));
    }

    @Test
    void shouldGenerateUniqueKeys() {
        assertEquals("202406071400_5",
                DateHelper.build().withDateDef("2024-06-07 14:03:00").uniqueKeyPer5Min());
        assertEquals("202406071403_1",
                DateHelper.build().withDateDef("2024-06-07 14:03:45").uniqueKeyPer1Min());
    }

    @Test
    void shouldComputeMonthBoundaries() {
        DateHelper mid = DateHelper.build().withDateDef("2024-06-15 10:30:00");

        assertEquals("2024-06-01 00:00:00", mid.copy().monthFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-30 23:59:59", mid.copy().monthLastDate().toyyyyMMddHHmmss());
        assertEquals("2024-07-01 00:00:00", mid.copy().monthEndExclusive().toyyyyMMddHHmmss());

        DateHelper.DatePeriod period = mid.monthRangeExclusive();
        assertEquals("2024-06-01 00:00:00",
                DateHelper.of(period.getStartInclusive()).toyyyyMMddHHmmss());
        assertEquals("2024-07-01 00:00:00",
                DateHelper.of(period.getEndExclusive()).toyyyyMMddHHmmss());
        assertTrue(period.contains(DateHelper.build().withDateDef("2024-06-30 23:59:59").toDate()));
        assertFalse(period.contains(DateHelper.build().withDateDef("2024-07-01 00:00:00").toDate()));
    }

    @Test
    void shouldComputeWeekBoundaries() {
        // 2024-06-07 周五
        DateHelper fri = DateHelper.build().withDateDef("2024-06-07 15:00:00");

        assertEquals("2024-06-03 00:00:00", fri.copy().weekFirstDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-09 23:59:59", fri.copy().weekLastDate().toyyyyMMddHHmmss());
        assertEquals("2024-06-10 00:00:00", fri.copy().weekEndExclusive().toyyyyMMddHHmmss());

        DateHelper.DatePeriod week = fri.weekRangeExclusive();
        assertEquals(7, DateHelper.of(week.getStartInclusive())
                .daysUntil(week.getEndExclusive()));
    }

    @Test
    void shouldComputeYearAndQuarterBoundaries() {
        DateHelper d = DateHelper.build().withDateDef("2024-08-20 12:00:00");

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
                DateHelper.build().withDateDef("2024-06-15 00:00:00")
                        .firstDayOfPreviousMonth().toyyyyMMddHHmmss());
        assertEquals("2024-05-27 00:00:00",
                DateHelper.build().withDateDef("2024-06-07 00:00:00")
                        .firstDayOfPreviousWeek().toyyyyMMddHHmmss());
    }

    @Test
    void shouldComputeDaySpan() {
        Date a = DateHelper.build().withDateDef("2024-06-01 00:00:00").toDate();
        Date b = DateHelper.build().withDateDef("2024-06-08 00:00:00").toDate();
        assertEquals(7, DateHelper.of(a).daysUntil(b));
        assertEquals(7, DateHelper.of(a).daysBetween(b));
    }

    @Test
    void shouldCompareSameDayAndMonth() {
        DateHelper a = DateHelper.build().withDateDef("2024-06-07 08:00:00");
        Date morning = DateHelper.build().withDateDef("2024-06-07 01:00:00").toDate();
        Date otherMonth = DateHelper.build().withDateDef("2024-07-07 01:00:00").toDate();

        assertTrue(a.isSameDay(morning));
        assertTrue(a.isSameMonth(morning));
        assertFalse(a.isSameMonth(otherMonth));
    }
}
