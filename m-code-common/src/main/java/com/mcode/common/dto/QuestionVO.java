package com.mcode.common.dto;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.mcode.common.enums.DifficultyEnum;
import com.mcode.common.enums.QuestionTypeEnum;
import lombok.Data;

@Data
public class QuestionVO {
    private Long id;
    private String title;
    private String description;
    private QuestionTypeEnum type;
    private Long sectionId;
    private String templateCode;
    private String testCases;
    private String options;
    private String correctAnswer;
    private String referenceAnswer;
    private DifficultyEnum difficulty;
    private Long categoryId;
    private Integer acceptedCount;
    private Integer submissionCount;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
