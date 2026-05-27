package com.mcode.ai.dto;

import lombok.Data;

@Data
public class ScoreRequest {
    private String questionDescription;
    private String referenceAnswer;
    private String userAnswer;
}
