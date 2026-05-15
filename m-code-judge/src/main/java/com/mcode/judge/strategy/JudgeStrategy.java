package com.mcode.judge.strategy;

import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.judge.entity.Submission;

public interface JudgeStrategy {

    QuestionTypeEnum supportedType();

    Submission judge(Long userId, Long questionId, String answer, Integer language, String questionJson);
}
