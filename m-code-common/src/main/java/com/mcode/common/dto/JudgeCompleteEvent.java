package com.mcode.common.dto;

import lombok.Data;

@Data
public class JudgeCompleteEvent {
    private Long submissionId;
    private Long questionId;
    private Long userId;
    private String status;
    private Integer timeUsed;
    private Integer memoryUsed;
}
