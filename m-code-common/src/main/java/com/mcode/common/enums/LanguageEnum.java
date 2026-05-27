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
    public static LanguageEnum fromDesc(String desc) {
        for (LanguageEnum type : values()) {
            if (type.desc.equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + desc);
    }
}
