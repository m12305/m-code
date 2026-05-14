package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
    private final Integer code;
    private final String desc;

    LanguageEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
