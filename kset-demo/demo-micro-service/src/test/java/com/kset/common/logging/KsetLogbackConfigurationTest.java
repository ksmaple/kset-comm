package com.kset.common.logging;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class KsetLogbackConfigurationTest {

    @Test
    void includesFlowErrorFieldsInJsonMdc() throws IOException {
        String xml = readResource("kset-logback-file-appenders.xml");

        assertThat(xml).contains("<includeMdcKeyName>flow.errorType</includeMdcKeyName>");
        assertThat(xml).contains("<includeMdcKeyName>flow.errorMessage</includeMdcKeyName>");
    }

    @Test
    void infoFileAppenderOnlyAcceptsInfoLevel() throws IOException {
        String xml = readResource("kset-logback-file-appenders.xml");
        String infoAppender = xml.substring(
                xml.indexOf("<appender name=\"KSET_INFO_FILE\""),
                xml.indexOf("<appender name=\"KSET_WARN_FILE\""));

        assertThat(infoAppender).contains("ch.qos.logback.classic.filter.LevelFilter");
        assertThat(infoAppender).contains("<level>INFO</level>");
        assertThat(infoAppender).contains("<onMatch>ACCEPT</onMatch>");
        assertThat(infoAppender).contains("<onMismatch>DENY</onMismatch>");
    }

    @Test
    void textConsolePatternUsesCompactLayout() throws IOException {
        String xml = readResource("kset-logback-spring.xml");

        assertThat(xml).contains("%d{HH:mm:ss.SSS} %level [%X{traceId:-}/%X{spanId:-}] %logger{32} - %msg%n");
        assertThat(xml).doesNotContain("%-5level");
        assertThat(xml).doesNotContain("[%X{grayTag:-}] [%X{operator:-}]");
    }

    private static String readResource(String name) throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
            assertThat(input).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
