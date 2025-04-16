package com.example.entity.ot;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum SexEnum implements IEnum<String> {
    MALE("男"),
    FEMALE("女");

    private final String value;

    SexEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
    
    /**
     * 从字符串转换为枚举值，支持中文和枚举名称
     * @param value 字符串值（如"男"、"女"、"MALE"、"FEMALE"）
     * @return 对应的枚举值，如果无法转换则返回null
     */
    public static SexEnum fromString(String value) {
        if (value == null) return null;
        
        // 先尝试从枚举名称转换
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ignored) {
            // 如果不是枚举名称，则尝试中文转换
            for (SexEnum sexEnum : values()) {
                if (sexEnum.getValue().equals(value)) {
                    return sexEnum;
                }
            }
            // 仍然无法转换
            return null;
        }
    }
}