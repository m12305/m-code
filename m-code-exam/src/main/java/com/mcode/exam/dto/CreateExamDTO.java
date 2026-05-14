package com.mcode.exam.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateExamDTO {
    private String title;
    private String description;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ExamQuestionDTO> questions;

    @Data
    public static class ExamQuestionDTO {
        private Long questionId;
        private Integer score;
        private Integer sort;
    }
}
