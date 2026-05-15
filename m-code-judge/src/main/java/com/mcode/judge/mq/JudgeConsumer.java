package com.mcode.judge.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.JudgeResultMapper;
import com.mcode.judge.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeConsumer {

    private static final String CACHE_KEY_PREFIX = "question:detail:";

    private final SubmissionMapper submissionMapper;
    private final JudgeResultMapper judgeResultMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleJudge(Long submissionId) {
        log.info("收到判题任务: submissionId={}", submissionId);
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            log.warn("提交记录不存在: submissionId={}", submissionId);
            return;
        }

        String cacheKey = CACHE_KEY_PREFIX + submission.getQuestionId();
        String questionJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (questionJson == null) {
            submission.setStatus(JudgeStatusEnum.RUNTIME_ERROR);
            submissionMapper.updateById(submission);
            log.warn("题目缓存不存在，无法判题: submissionId={}", submissionId);
            return;
        }

        submission.setStatus(JudgeStatusEnum.RUNNING);
        submissionMapper.updateById(submission);

        try {
            doJudge(submission, questionJson);
        } catch (Exception e) {
            log.error("判题异常: submissionId={}", submissionId, e);
            submission.setStatus(JudgeStatusEnum.RUNTIME_ERROR);
            submissionMapper.updateById(submission);
        }
    }

    private void doJudge(Submission submission, String questionJson) throws Exception {
        String testCasesStr = objectMapper.readTree(questionJson).get("testCases").asText();

        // 临时实现：如果无测试用例则直接通过
        if (testCasesStr == null || testCasesStr.isEmpty()) {
            submission.setStatus(JudgeStatusEnum.ACCEPTED);
            submission.setTimeUsed(0);
            submission.setMemoryUsed(0);
            submissionMapper.updateById(submission);

            JudgeResult result = new JudgeResult();
            result.setSubmissionId(submission.getId());
            result.setTestCaseName("默认");
            result.setStatus(JudgeStatusEnum.ACCEPTED);
            result.setTimeUsed(0);
            result.setMemoryUsed(0);
            judgeResultMapper.insert(result);
            log.info("编程题判题完成（无测试用例）: submissionId={}", submission.getId());
            return;
        }

        // TODO: 真正的代码编译运行与测试用例比对，当前暂时标记通过
        submission.setStatus(JudgeStatusEnum.ACCEPTED);
        submission.setTimeUsed(100);
        submission.setMemoryUsed(10240);
        submissionMapper.updateById(submission);

        JudgeResult result = new JudgeResult();
        result.setSubmissionId(submission.getId());
        result.setTestCaseName("默认");
        result.setStatus(JudgeStatusEnum.ACCEPTED);
        result.setTimeUsed(100);
        result.setMemoryUsed(10240);
        judgeResultMapper.insert(result);

        log.info("编程题判题完成: submissionId={}, status={}", submission.getId(), submission.getStatus());
    }
}
