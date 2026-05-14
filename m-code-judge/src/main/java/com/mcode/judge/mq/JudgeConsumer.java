package com.mcode.judge.mq;

import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeConsumer {

    private final SubmissionMapper submissionMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleJudge(Long submissionId) {
        log.info("收到判题任务: submissionId={}", submissionId);
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) return;

        // 模拟判题过程：默认通过
        submission.setStatus(JudgeStatusEnum.ACCEPTED);
        submission.setTimeUsed(100);
        submission.setMemoryUsed(10240);
        submissionMapper.updateById(submission);

        log.info("判题完成: submissionId={}, status={}", submissionId, submission.getStatus());
    }
}
