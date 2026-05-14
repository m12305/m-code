package com.mcode.judge.dto;

import com.mcode.common.enums.LanguageEnum;
import lombok.Data;

@Data
public class SubmitCodeDTO {
    private Long questionId;
    private String code;
    private Integer language;
}
