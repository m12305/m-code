package com.mcode.exam.dto;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.LanguageEnum;

import lombok.Data;

@Data
public class JudgeSubmissionVO {
    private Long id;
    private Long userId;
    private Long questionId;
    private String answer;
    private LanguageEnum language;
    private JudgeStatusEnum status;
    private Integer timeUsed;
    private Integer memoryUsed;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}

