package com.mcode.judge.strategy;

import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortAnswerJudgeStrategy implements JudgeStrategy {

    private final SubmissionMapper submissionMapper;

    @Override
    public QuestionTypeEnum supportedType() {
        return QuestionTypeEnum.SHORT_ANSWER;
    }

    @Override
    public Submission judge(Long userId, Long questionId, String answer, Integer language, String questionJson) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(questionId);
        submission.setAnswer(answer);
        submission.setStatus(JudgeStatusEnum.PENDING);
        submissionMapper.insert(submission);

        log.info("简答题提交待人工/AI评判: submissionId={}", submission.getId());
        return submission;
    }
}
