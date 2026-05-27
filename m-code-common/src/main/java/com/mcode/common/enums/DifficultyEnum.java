package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import lombok.Getter;

//题目难度枚举
@Getter
public enum DifficultyEnum {

    EASY(1, "简单"),
    MEDIUM(2, "中等"),
    HARD(3, "困难");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;

    DifficultyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator  // 反序列化时根据 desc 查找枚举
    public static DifficultyEnum fromDesc(String desc) {
        for (DifficultyEnum type : values()) {
            if (type.desc.equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type: " + desc);
    }
}
