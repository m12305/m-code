package com.mcode.judge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.LanguageEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("submission")
public class Submission extends BaseEntity {

    private Long userId;
    private Long questionId;
    private String code;
    private LanguageEnum language;
    private JudgeStatusEnum status;
    private Integer timeUsed;
    private Integer memoryUsed;
}
