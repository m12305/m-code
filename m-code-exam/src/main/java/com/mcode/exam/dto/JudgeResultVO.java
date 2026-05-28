package com.mcode.exam.dto;

import com.mcode.common.enums.JudgeStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JudgeResultVO {
    private Long id;
    private Long submissionId;
    private String testCaseName;
    private JudgeStatusEnum status;
    private String actualOutput;
    private String errorMessage;
    private Integer timeUsed;
    private Integer memoryUsed;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
