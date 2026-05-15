package com.mcode.judge.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.common.exception.BusinessException;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChoiceJudgeStrategy implements JudgeStrategy {

    private final SubmissionMapper submissionMapper;
    private final ObjectMapper objectMapper;

    @Override
    public QuestionTypeEnum supportedType() {
        return QuestionTypeEnum.MULTIPLE_CHOICE;
    }

    @Override
    public Submission judge(Long userId, Long questionId, String answer, Integer language, String questionJson) {
        Submission submission = createSubmission(userId, questionId, answer);
        try {
            JsonNode question = objectMapper.readTree(questionJson);
            String correctAnswer = question.get("correctAnswer").asText();
            if (correctAnswer != null && correctAnswer.equalsIgnoreCase(answer)) {
                submission.setStatus(JudgeStatusEnum.ACCEPTED);
            } else {
                submission.setStatus(JudgeStatusEnum.WRONG_ANSWER);
            }
        } catch (Exception e) {
            log.error("选择题判题异常: submissionId={}", submission.getId(), e);
            throw new BusinessException("判题过程出错");
        }
        submissionMapper.updateById(submission);
        return submission;
    }

    private Submission createSubmission(Long userId, Long questionId, String answer) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(questionId);
        submission.setAnswer(answer);
        submission.setStatus(JudgeStatusEnum.PENDING);
        submissionMapper.insert(submission);
        return submission;
    }
}
