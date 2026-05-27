package com.mcode.ai.dto;

import lombok.Data;

@Data
public class ExplainRequest {
    private String code;
    private String language;
}
