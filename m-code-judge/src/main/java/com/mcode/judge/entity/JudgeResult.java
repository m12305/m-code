package com.mcode.judge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import com.mcode.common.enums.JudgeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("judge_result")
public class JudgeResult extends BaseEntity {

    private Long submissionId;
    private String testCaseName;
    private JudgeStatusEnum status;
    private String actualOutput;
    private String errorMessage;
    private Integer timeUsed;
    private Integer memoryUsed;
}
