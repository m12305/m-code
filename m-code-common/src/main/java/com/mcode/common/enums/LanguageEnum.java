package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

//编程语言枚举

@Getter
public enum LanguageEnum {

    JAVA(1, "Java"),
    PYTHON(2, "Python"),
    CPP(3, "C++"),
    C(4, "C"),
    JAVASCRIPT(5, "JavaScript"),
    GO(6, "Go");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    LanguageEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator  // 反序列化时根据 desc 查找枚举
    public static LanguageEnum fromValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("枚举值不能为空");
        }

        // 情况1：传入的是数字 Integer → 按 code 匹配
        if (value instanceof Integer code) {
            for (LanguageEnum type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
        }

        // 情况2：传入的是字符串 → 先尝试转数字，再尝试匹配描述
        String str = value.toString().trim();
        try {
            int code = Integer.parseInt(str);
            for (LanguageEnum type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
        } catch (NumberFormatException e) {
            // 不是数字 → 按文字 desc 匹配
            for (LanguageEnum type : values()) {
                if (type.getDesc().equals(str)) {
                    return type;
                }
            }
        }

        // 都不匹配
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
