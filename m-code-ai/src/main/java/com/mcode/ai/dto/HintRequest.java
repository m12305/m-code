package com.mcode.ai.dto;

import lombok.Data;

@Data
public class HintRequest {
    private String problemDescription;
    private String difficulty;
    private String questionType;
}
