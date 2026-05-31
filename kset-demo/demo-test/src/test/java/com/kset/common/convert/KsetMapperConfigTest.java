package com.kset.common.convert;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class KsetMapperConfigTest {

    @Test
    void inheritsGlobalConfigAndConversionSupport() {
        SampleMapper mapper = Mappers.getMapper(SampleMapper.class);

        SampleSource source = new SampleSource();
        source.setName("alice");
        source.setDeleted(1);

        SampleTarget target = mapper.toTarget(source);

        assertThat(target.getName()).isEqualTo("alice");
        assertThat(target.getRemoved()).isTrue();
    }

    @Mapper(config = KsetMapperConfig.class)
    interface SampleMapper {

        @Mapping(source = "deleted", target = "removed", qualifiedByName = "integerToBoolean")
        SampleTarget toTarget(SampleSource source);
    }

    static class SampleSource {

        private String name;
        private Integer deleted;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getDeleted() {
            return deleted;
        }

        public void setDeleted(Integer deleted) {
            this.deleted = deleted;
        }
    }

    static class SampleTarget {

        private String name;
        private Boolean removed;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getRemoved() {
            return removed;
        }

        public void setRemoved(Boolean removed) {
            this.removed = removed;
        }
    }
}
