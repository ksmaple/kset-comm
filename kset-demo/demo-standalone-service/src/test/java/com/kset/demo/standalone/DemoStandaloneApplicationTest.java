package com.kset.demo.standalone;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoStandaloneApplicationTest {

    @Test
    void shouldExposeApplicationEntryPoint() {
        assertThat(DemoStandaloneApplication.class.getPackageName())
                .isEqualTo("com.kset.demo.standalone");
    }
}
