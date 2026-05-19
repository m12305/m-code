package com.mcode.common.dto;

import lombok.Data;

@Data
public class SubmitCodeDTO {
    private Long questionId;
    private String answer;
    private Integer language;
}
