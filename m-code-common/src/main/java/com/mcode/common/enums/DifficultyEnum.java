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
    public static DifficultyEnum fromValue(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("枚举值不能为空");
        }

        // 情况1：传入的是数字 Integer → 按 code 匹配
        if (value instanceof Integer code) {
            for (DifficultyEnum type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
        }

        // 情况2：传入的是字符串 → 先尝试转数字，再尝试匹配描述
        String str = value.toString().trim();
        try {
            int code = Integer.parseInt(str);
            for (DifficultyEnum type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
        } catch (NumberFormatException e) {
            // 不是数字 → 按文字 desc 匹配
            for (DifficultyEnum type : values()) {
                if (type.getDesc().equals(str)) {
                    return type;
                }
            }
        }

        // 都不匹配
        throw new IllegalArgumentException("Unknown question type: " + value);
    }
}
