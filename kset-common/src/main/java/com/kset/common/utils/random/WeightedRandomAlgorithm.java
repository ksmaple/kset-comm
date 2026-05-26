package com.kset.common.utils.random;

/**
 * 加权随机算法。
 */
public enum WeightedRandomAlgorithm {
    /** 等权随机 */
    UNIFORM,
    /** 轮盘法 */
    WHEEL,
    /** 整数配额 + 剩余轮盘，适合极小概率 */
    QUOTA
}
