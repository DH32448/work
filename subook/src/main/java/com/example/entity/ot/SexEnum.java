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
}