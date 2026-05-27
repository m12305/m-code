package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum QuestionTypeEnum {

    PROGRAMMING(1, "编程题"),
    MULTIPLE_CHOICE(2, "选择题"),
    SHORT_ANSWER(3, "简答题"),
    TRUE_FALSE(4, "判断题");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    QuestionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator  // 反序列化时根据 desc 查找枚举
    public static QuestionTypeEnum fromDesc(String desc) {
        for (QuestionTypeEnum type : values()) {
            if (type.desc.equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + desc);
    }
}
