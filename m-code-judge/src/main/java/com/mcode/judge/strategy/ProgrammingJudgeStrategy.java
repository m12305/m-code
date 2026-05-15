package com.mcode.judge.strategy;

import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.LanguageEnum;
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
public class ProgrammingJudgeStrategy implements JudgeStrategy {

    private final SubmissionMapper submissionMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public QuestionTypeEnum supportedType() {
        return QuestionTypeEnum.PROGRAMMING;
    }

    @Override
    public Submission judge(Long userId, Long questionId, String answer, Integer language, String questionJson) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(questionId);
        submission.setAnswer(answer);
        if (language != null) {
            submission.setLanguage(LanguageEnum.values()[language - 1]);
        }
        submission.setStatus(JudgeStatusEnum.PENDING);
        submissionMapper.insert(submission);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, submission.getId());
        log.info("编程题提交已入队: submissionId={}", submission.getId());
        return submission;
    }
}
