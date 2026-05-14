package com.mcode.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

//判题状态枚举

@Getter
public enum JudgeStatusEnum {

    PENDING(0, "等待判题"),
    RUNNING(1, "判题中"),
    ACCEPTED(2, "通过"),
    WRONG_ANSWER(3, "答案错误"),
    COMPILE_ERROR(4, "编译错误"),
    RUNTIME_ERROR(5, "运行错误"),
    TIME_LIMIT_EXCEEDED(6, "运行超时"),
    MEMORY_LIMIT_EXCEEDED(7, "内存溢出");

    @EnumValue
    private final Integer code;
    private final String desc;

    JudgeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
