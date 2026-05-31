package com.kset.common.convert;

import org.mapstruct.Named;

/**
 * MapStruct 公共类型转换（PO Integer 布尔列、枚举 code 等）。
 * <p>
 * 在 Mapper 方法上通过 {@code @Mapping(..., qualifiedByName = "...")} 引用。
 */
public final class KsetConversionSupport {

    private KsetConversionSupport() {
    }

    /** PO 逻辑删除/开关列（0/1）→ Entity {@code Boolean}。 */
    @Named("integerToBoolean")
    public static Boolean integerToBoolean(Integer value) {
        if (value == null) {
            return null;
        }
        return value != 0;
    }

    /** Entity {@code Boolean} → PO 逻辑删除/开关列（0/1）。 */
    @Named("booleanToInteger")
    public static Integer booleanToInteger(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? 1 : 0;
    }
}
