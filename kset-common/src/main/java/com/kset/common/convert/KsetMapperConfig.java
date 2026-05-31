package com.kset.common.convert;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * KSet 全局 MapStruct 配置：业务 {@code @Mapper} 通过 {@code config = KsetMapperConfig.class} 继承。
 * <p>
 * 约定见 conversion-spec：Spring 注入、未映射字段告警、公共类型转换见 {@link KsetConversionSupport}。
 */
@MapperConfig(
        componentModel = "spring",
        uses = KsetConversionSupport.class,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface KsetMapperConfig {
}
