package com.mcode.judge.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.JudgeResultMapper;
import com.mcode.judge.mapper.SubmissionMapper;
import com.mcode.judge.utils.Judge0Client;
import com.mcode.judge.utils.Judge0Result;
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
    private final Judge0Client judge0Client;

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
        JsonNode question = objectMapper.readTree(questionJson);
        String testCasesStr = question.has("testCases") && !question.get("testCases").isNull()
                ? question.get("testCases").asText() : null;

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

        JsonNode testCases = objectMapper.readTree(testCasesStr);
        Integer languageCode = submission.getLanguage() != null ? submission.getLanguage().getCode() : null;

        int maxTime = 0;
        int maxMemory = 0;
        JudgeStatusEnum finalStatus = JudgeStatusEnum.ACCEPTED;

        for (JsonNode tc : testCases) {
            String input = tc.has("input") ? tc.get("input").asText() : "";
            String expectedOutput = tc.has("output") ? tc.get("output").asText() : "";

            String testCaseName = tc.has("name") ? tc.get("name").asText() : "TestCase";

            Judge0Result jr = judge0Client.judge(submission.getAnswer(), languageCode, input, expectedOutput);
            JudgeStatusEnum status = judge0Client.mapStatus(jr.getStatusId());

//            JudgeResult result = new JudgeResult();
//            result.setSubmissionId(submission.getId());
//            result.setTestCaseName(testCaseName);
//            result.setStatus(status);
//            result.setActualOutput(jr.getStdout());
//            result.setErrorMessage(jr.getStderr() != null ? jr.getStderr() : jr.getCompileOutput());
//            result.setTimeUsed(parseTime(jr.getTime()));
//            result.setMemoryUsed(jr.getMemory());
//            judgeResultMapper.insert(result);

            maxTime = Math.max(maxTime, parseTime(jr.getTime()));
            maxMemory = Math.max(maxMemory, jr.getMemory());

            if (status != JudgeStatusEnum.ACCEPTED && finalStatus == JudgeStatusEnum.ACCEPTED) {
                finalStatus = status;
            }
        }

        submission.setStatus(finalStatus);
        submission.setTimeUsed(maxTime);
        submission.setMemoryUsed(maxMemory);
        submissionMapper.updateById(submission);

        log.info("编程题判题完成: submissionId={}, status={}, time={}ms, memory={}KB",
                submission.getId(), finalStatus, maxTime, maxMemory);
    }

    private int parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return 0;
        }
        try {
            return (int) (Double.parseDouble(timeStr) * 1000);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
