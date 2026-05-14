package com.mcode.question.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import com.mcode.common.enums.DifficultyEnum;
import com.mcode.common.enums.QuestionTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {

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
}
