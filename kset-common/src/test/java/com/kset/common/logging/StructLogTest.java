package com.kset.common.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StructLogTest {

    @Test
    void delegatesToBoundLogger() {
        Logger logger = mock(Logger.class);
        when(logger.isInfoEnabled()).thenReturn(true);
        StructLog structLog = StructLog.of(logger);
        structLog.info("event", "key", "value");
        verify(logger).info("event", LogUtil.toArgs("key", "value"));
    }
}
