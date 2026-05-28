package com.mcode.judge.strategy;

import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.SubmissionMapper;
import com.mcode.judge.mq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortAnswerJudgeStrategy implements JudgeStrategy {

    private final SubmissionMapper submissionMapper;
    private final RabbitTemplate rabbitTemplate;

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

        rabbitTemplate.convertAndSend(RabbitMQConfig.SHORT_ANSWER_EXCHANGE, RabbitMQConfig.SHORT_ANSWER_ROUTING_KEY, submission.getId());
        log.info("简答题提交已入队: submissionId={}", submission.getId());
        return submission;
    }
}
