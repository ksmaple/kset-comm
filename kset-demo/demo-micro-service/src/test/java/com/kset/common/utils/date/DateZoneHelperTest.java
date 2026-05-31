package com.kset.common.utils.date;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateZoneHelperTest {

    @Test
    void shouldParseZoneByHourOffset() {
        assertEquals(ZoneOffset.ofHours(8), DateZoneHelper.zoneOf(8).getRules().getOffset(Instant.now()));
        assertEquals(ZoneOffset.ofHours(-5), DateZoneHelper.zoneOf(-5).getRules().getOffset(Instant.now()));
        assertEquals(DateZone.CN.toZoneId(), DateZoneHelper.parseZone("8"));
        assertEquals(DateZone.CN.toZoneId(), DateZoneHelper.parseZone("+8"));
        assertEquals(DateZone.CN.toZoneId(), DateZoneHelper.parseZone("GMT+8"));
        assertEquals(DateZone.CN.toZoneId(), DateZoneHelper.parseZone("UTC+8"));
        assertEquals(DateZone.IN.toZoneId(), DateZoneHelper.parseZone("GMT+05:30"));
        assertEquals(DateZone.SAU.toZoneId(), DateZoneHelper.parseZone("SAU"));
    }

    @Test
    void shouldUseDateZoneEnum() {
        assertEquals("GMT+8", DateZone.CN.toGmtLabel());
        assertEquals(DateZone.SAU, DateZone.ofHours(3));
        assertNull(DateZone.ofHours(99));
        assertEquals(DateZone.CN, DateZone.parse("CN"));
        assertEquals(DateZone.CN, DateZone.parse("GMT+8"));
    }

    @Test
    void shouldParseWithZone() {
        assertEquals(DateZone.SAU.toZoneId(),
                DateZoneHelper.of(System.currentTimeMillis(), DateZone.SAU).getZone());
        assertEquals(DateZone.CN.toZoneId(),
                DateZoneHelper.of(System.currentTimeMillis(), 8).getZone());
    }

    @Test
    void shouldConvertBetweenZones() {
        DateZoneHelper cn = DateZoneHelper.of(1_700_000_000_000L, DateZone.CN);
        String cnWall = cn.copy().format(DateHelper.PATTERN_DEF);

        String sauWall = cn.copy().toZone("GMT+3").format(DateHelper.PATTERN_DEF);
        assertNotEquals(cnWall, sauWall);

        assertEquals(cn.toEpochMilli(), cn.copy().toSAU().toEpochMilli());
    }

    @Test
    void shouldFormatInTargetZone() {
        Date date = DateHelper.parse("2024-06-07 14:05:30").toDate();
        String utc = DateZoneHelper.format(date, DateZone.UTC, DateHelper.PATTERN_DEF);
        String cn = DateZoneHelper.format(date, 8, DateHelper.PATTERN_DEF);
        assertNotEquals(utc, cn);
    }

    @Test
    void shouldBridgeWithDateHelper() {
        DateHelper local = DateHelper.parse("2024-06-07 14:05:30");
        DateZoneHelper zoned = DateZoneHelper.ofLocal(local).toCN();
        DateHelper back = zoned.toLocal();
        assertEquals(local.toyyyyMMddHHmmss(), back.toyyyyMMddHHmmss());
    }

    @Test
    void shouldConvertSauWallClockToLocal() {
        LocalDateTime sau = LocalDateTime.of(2024, 6, 7, 10, 0, 0);
        DateHelper local = DateZoneHelper.wallClockToLocal(sau, DateZone.SAU);

        Instant expected = sau.atZone(DateZone.SAU.toZoneId()).toInstant();
        Instant actual = local.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant();
        assertEquals(expected, actual);
        assertEquals(sau, DateZoneHelper.localToWallClock(local, DateZone.SAU));
    }

    @Test
    void shouldConvertByHourOffset() {
        LocalDateTime cnWall = LocalDateTime.of(2024, 6, 7, 18, 0, 0);
        DateHelper local = DateZoneHelper.wallClockToLocal(cnWall, 8);
        assertEquals(cnWall, DateZoneHelper.localToWallClock(local, 8));
    }

    @Test
    void shouldConvertLocalWallClockToSau() {
        DateHelper local = DateHelper.parse("2024-06-07 15:00:00");
        LocalDateTime sau = DateZoneHelper.localToSau(local);

        Instant expected = local.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant();
        Instant actual = sau.atZone(DateZone.SAU.toZoneId()).toInstant();
        assertEquals(expected, actual);
    }

    @Test
    void shouldParseSauStringToLocal() {
        DateHelper local = DateZoneHelper.sauToLocalDef("2024-06-07 10:00:00");
        assertEquals(LocalDateTime.of(2024, 6, 7, 10, 0, 0), DateZoneHelper.localToSau(local));
        assertEquals("2024-06-07 10:00:00", DateZoneHelper.localToSauDef(local));
    }
}
