package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

//题目难度枚举
@Getter
public enum DifficultyEnum {

    EASY(1, "简单"),
    MEDIUM(2, "中等"),
    HARD(3, "困难");

    @EnumValue
    private final Integer code;
    private final String desc;

    DifficultyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
