package com.kset.mysql.autoconfigure;

import com.kset.mysql.config.KsetMysqlMonitorProperties;
import com.kset.mysql.interceptor.SlowSqlMonitorInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis-Plus 约定由 {@code application-kset-mysql.yml} 提供默认配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(KsetMysqlMonitorProperties.class)
public class KsetMybatisPlusConfiguration {

    @Bean
    @ConditionalOnBean(SqlSessionFactory.class)
    @ConditionalOnProperty(prefix = "kset.mysql.slow-sql", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SlowSqlMonitorInterceptor slowSqlMonitorInterceptor(KsetMysqlMonitorProperties properties) {
        SlowSqlMonitorInterceptor interceptor = new SlowSqlMonitorInterceptor();
        interceptor.setThresholdMs(properties.getThresholdMs());
        return interceptor;
    }

    @Bean
    @ConditionalOnBean({SqlSessionFactory.class, SlowSqlMonitorInterceptor.class})
    public Object slowSqlMonitorInterceptorRegistrar(SqlSessionFactory sqlSessionFactory,
                                                     SlowSqlMonitorInterceptor interceptor) {
        sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        return new Object();
    }
}
