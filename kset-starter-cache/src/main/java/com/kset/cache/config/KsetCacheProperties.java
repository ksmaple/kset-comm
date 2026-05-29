package com.kset.cache.config;

import com.kset.cache.core.KsetCacheLayer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "kset.cache")
public class KsetCacheProperties {

    private boolean enabled = true;
    private List<KsetCacheLayer> defaultLayers = new ArrayList<>(List.of(KsetCacheLayer.L1, KsetCacheLayer.L2));
    private boolean cacheNull = true;
    private Duration nullTtl = Duration.ofMinutes(1);
    private boolean singleFlightEnabled = true;
    private final L1 l1 = new L1();
    private final L2 l2 = new L2();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<KsetCacheLayer> getDefaultLayers() {
        return defaultLayers;
    }

    public void setDefaultLayers(List<KsetCacheLayer> defaultLayers) {
        this.defaultLayers = defaultLayers != null ? new ArrayList<>(defaultLayers) : new ArrayList<>();
    }

    public boolean isCacheNull() {
        return cacheNull;
    }

    public void setCacheNull(boolean cacheNull) {
        this.cacheNull = cacheNull;
    }

    public Duration getNullTtl() {
        return nullTtl;
    }

    public void setNullTtl(Duration nullTtl) {
        this.nullTtl = nullTtl;
    }

    public boolean isSingleFlightEnabled() {
        return singleFlightEnabled;
    }

    public void setSingleFlightEnabled(boolean singleFlightEnabled) {
        this.singleFlightEnabled = singleFlightEnabled;
    }

    public L1 getL1() {
        return l1;
    }

    public L2 getL2() {
        return l2;
    }

    public static class L1 {
        private boolean enabled = true;
        private Duration defaultTtl = Duration.ofMinutes(5);
        private long maximumSize = 10_000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Duration getDefaultTtl() {
            return defaultTtl;
        }

        public void setDefaultTtl(Duration defaultTtl) {
            this.defaultTtl = defaultTtl;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
        }
    }

    public static class L2 {
        private boolean required = true;
        private Duration defaultTtl = Duration.ofMinutes(30);

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Duration getDefaultTtl() {
            return defaultTtl;
        }

        public void setDefaultTtl(Duration defaultTtl) {
            this.defaultTtl = defaultTtl;
        }
    }
}
