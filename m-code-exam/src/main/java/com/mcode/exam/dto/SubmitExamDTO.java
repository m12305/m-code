package com.mcode.exam.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitExamDTO {
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String answer;
        private Integer language;
    }
}
