package com.kset.common.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    void delegatesErrorWithThrowableAndStructuredFields() {
        Logger logger = mock(Logger.class);
        RuntimeException error = new RuntimeException("boom");
        when(logger.isErrorEnabled()).thenReturn(true);
        StructLog structLog = StructLog.of(logger);

        assertDoesNotThrow(() -> structLog.error("event failed", error, "key", "value"));

        Object[] expectedArgs = new Object[2];
        expectedArgs[0] = LogUtil.toArgs("key", "value")[0];
        expectedArgs[1] = error;
        verify(logger).error("event failed", expectedArgs);
    }
}
