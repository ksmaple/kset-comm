package com.kset.demo.gateway;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoGatewayApplicationTest {

    @Test
    void shouldExposeApplicationEntryPoint() {
        assertThat(DemoGatewayApplication.class.getPackageName())
                .isEqualTo("com.kset.demo.gateway");
    }
}
