package com.example.entity.ot;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 性别枚举类
 * 对应数据库中的enum('男', '女', '保密')类型
 */
public enum SexEnum implements IEnum<String> {
    MALE("男"),
    FEMALE("女"),
    SECRET("保密");  // 添加保密选项

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
     * @param value 字符串值（如"男"、"女"、"保密"、"MALE"、"FEMALE"、"SECRET"）
     * @return 对应的枚举值，如果无法转换则返回SECRET作为默认值
     */
    public static SexEnum fromString(String value) {
        if (value == null) return SECRET;  // 默认返回"保密"
        
        // 先尝试从枚举名称转换
        try {
            return valueOf(value.toUpperCase());  // 转换为大写以支持小写输入
        } catch (IllegalArgumentException ignored) {
            // 如果不是枚举名称，则尝试中文转换
            for (SexEnum sexEnum : values()) {
                if (sexEnum.getValue().equals(value)) {
                    return sexEnum;
                }
            }
            // 仍然无法转换，返回SECRET作为默认值
            return SECRET;
        }
    }
    
    /**
     * 获取所有可用的中文性别选项
     * @return 包含所有中文性别选项的列表
     */
    public static List<String> getAllChineseValues() {
        return Arrays.stream(values())
                .map(SexEnum::getValue)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有可用的枚举名称
     * @return 包含所有枚举名称的列表
     */
    public static List<String> getAllEnumNames() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查给定的值是否是有效的性别值（中文或枚举名称）
     * @param value 要检查的值
     * @return 如果是有效的性别值则返回true，否则返回false
     */
    public static boolean isValidSexValue(String value) {
        if (value == null) return false;
        
        // 检查是否是枚举名称
        try {
            valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException ignored) {
            // 检查是否是中文值
            for (SexEnum sexEnum : values()) {
                if (sexEnum.getValue().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}