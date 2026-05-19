package com.mcode.exam.dto;

import com.mcode.common.enums.JudgeStatusEnum;
import lombok.Data;

@Data
public class JudgeSubmissionVO {
    private Long id;
    private Long questionId;
    private JudgeStatusEnum status;
    private Integer timeUsed;
    private Integer memoryUsed;
}
