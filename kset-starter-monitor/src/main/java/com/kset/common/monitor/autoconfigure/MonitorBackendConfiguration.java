package com.kset.common.monitor.autoconfigure;

import com.kset.common.monitor.backend.LogBackend;
import com.kset.common.monitor.backend.MonitorBackend;
import com.kset.common.monitor.config.KsetMonitorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorBackendConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MonitorBackendConfiguration.class);

    /**
     * 默认监控后端：本地 SLF4J 日志输出；非 {@code log} 配置项当前均回落到 {@link LogBackend}。
     */
    @Bean
    @ConditionalOnMissingBean
    public MonitorBackend monitorBackend(KsetMonitorProperties properties) {
        String backend = properties.getBackend() != null ? properties.getBackend().trim().toLowerCase() : "log";
        long warnMs = properties.getSlowLog().getTransactionWarnMs();
        if (!"log".equals(backend)) {
            log.warn("kset.monitor.backend={} is not supported yet; using local LogBackend", backend);
        }
        return new LogBackend(warnMs);
    }
}
