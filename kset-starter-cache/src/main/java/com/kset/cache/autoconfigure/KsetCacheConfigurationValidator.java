package com.kset.cache.autoconfigure;

import com.kset.cache.config.KsetCacheProperties;
import com.kset.cache.core.KsetCacheLayer;
import com.kset.cache.core.KsetCacheStore;
import com.kset.cache.interceptor.KsetCacheOperation;
import com.kset.cache.interceptor.KsetCacheOperationParser;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KsetCacheConfigurationValidator implements SmartInitializingSingleton, ApplicationContextAware {

    private final List<KsetCacheStore> stores;
    private final KsetCacheProperties properties;
    private ApplicationContext applicationContext;

    public KsetCacheConfigurationValidator(List<KsetCacheStore> stores, KsetCacheProperties properties) {
        this.stores = stores;
        this.properties = properties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Set<KsetCacheLayer> availableLayers = stores.stream()
                .map(KsetCacheStore::layer)
                .collect(Collectors.toSet());
        for (KsetCacheLayer layer : properties.getDefaultLayers()) {
            requireLayer(availableLayers, layer, "kset.cache.default-layers includes " + layer);
        }
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> type = safeType(beanName);
            if (type == null || ClassUtils.isCglibProxyClass(type)) {
                continue;
            }
            for (Method method : type.getMethods()) {
                List<KsetCacheOperation> operations = KsetCacheOperationParser.parse(method);
                for (KsetCacheOperation operation : operations) {
                    for (KsetCacheLayer layer : operation.layers()) {
                        requireLayer(availableLayers, layer,
                                "Cache annotations on " + type.getName() + "#" + method.getName() + " require " + layer);
                    }
                }
            }
        }
    }

    private Class<?> safeType(String beanName) {
        try {
            Object bean = applicationContext.getBean(beanName);
            return AopUtils.getTargetClass(bean);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void requireLayer(Set<KsetCacheLayer> availableLayers, KsetCacheLayer layer, String message) {
        if (!availableLayers.contains(layer)) {
            throw new IllegalStateException(message + " but no " + layer + " cache store is available");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
