package com.mcode.exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import com.mcode.common.enums.JudgeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_answer")
public class ExamAnswer extends BaseEntity {

    private Long examId;
    private Long userId;
    private Long questionId;
    private String answer;
    private Integer language;
    private JudgeStatusEnum status;
    private Integer score;
    private Long submissionId;
}
