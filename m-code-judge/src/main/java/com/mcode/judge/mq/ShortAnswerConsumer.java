package com.mcode.judge.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.dto.QuestionVO;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.result.Result;
import com.mcode.judge.dto.ScoreRequest;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.feign.AiFeignClient;
import com.mcode.judge.feign.QuestionFeignClient;
import com.mcode.judge.mapper.JudgeResultMapper;
import com.mcode.judge.mapper.SubmissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortAnswerConsumer {

    private static final String CACHE_KEY_PREFIX = "question:detail:";

    private final SubmissionMapper submissionMapper;
    private final JudgeResultMapper judgeResultMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AiFeignClient aiFeignClient;
    private final QuestionFeignClient questionFeignClient;

    @RabbitListener(queues = RabbitMQConfig.SHORT_ANSWER_QUEUE)
    public void handleShortAnswerJudge(Long submissionId) {
        log.info("收到简答题判题任务: submissionId={}", submissionId);
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            log.warn("提交记录不存在: submissionId={}", submissionId);
            return;
        }

        String cacheKey = CACHE_KEY_PREFIX + submission.getQuestionId();
        String questionJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (questionJson == null) {
            questionJson = fetchQuestionFromFeign(submission.getQuestionId());
            if (questionJson == null) {
                submission.setStatus(JudgeStatusEnum.RUNTIME_ERROR);
                submissionMapper.updateById(submission);
                log.warn("题目不存在，无法判题: submissionId={}", submissionId);
                return;
            }
        }

        submission.setStatus(JudgeStatusEnum.RUNNING);
        submissionMapper.updateById(submission);

        try {
            doJudge(submission, questionJson);
        } catch (Exception e) {
            log.error("简答题判题异常: submissionId={}", submissionId, e);
            submission.setStatus(JudgeStatusEnum.RUNTIME_ERROR);
            submissionMapper.updateById(submission);
        }
    }

    private void doJudge(Submission submission, String questionJson) throws Exception {
        JsonNode question = objectMapper.readTree(questionJson);

        String title = question.has("title") ? question.get("title").asText() : "";
        String description = question.has("description") ? question.get("description").asText() : "";
        String referenceAnswer = question.has("referenceAnswer") ? question.get("referenceAnswer").asText() : "";

        String questionDescription = title;
        if (!description.isEmpty()) {
            questionDescription += "\n" + description;
        }

        // Call AI score endpoint
        //Map<String, String> request = new HashMap<>();
        //request.put("questionDescription", questionDescription);
        //request.put("referenceAnswer", referenceAnswer);
        //request.put("userAnswer", submission.getAnswer());

        ScoreRequest request = new ScoreRequest();
        request.setQuestionDescription(questionDescription);
        request.setReferenceAnswer(referenceAnswer);
        request.setUserAnswer(submission.getAnswer());

        Result<String> result = aiFeignClient.score(request);
        String aiResponse = result != null ? result.getData() : null;
        if (aiResponse == null) {
            throw new RuntimeException("AI评分返回为空");
        }

        // Parse AI response to extract score & feedback
        JsonNode scoreJson = objectMapper.readTree(aiResponse);
        int score = scoreJson.has("score") ? scoreJson.get("score").asInt() : 0;
        String feedback = scoreJson.has("feedback") ? scoreJson.get("feedback").asText() : "";
        String correctPoints = scoreJson.has("correctPoints") ? scoreJson.get("correctPoints").toString() : "";
        String missedPoints = scoreJson.has("missedPoints") ? scoreJson.get("missedPoints").toString() : "";
        String suggestions = scoreJson.has("suggestions") ? scoreJson.get("suggestions").asText() : "";

        feedback = feedback + "\n 答对要点：" +correctPoints + "\n 遗漏要点：" +missedPoints + "\n 改进建议：" + suggestions;

        // Store full AI response as judge result
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setSubmissionId(submission.getId());
        judgeResult.setTestCaseName("AI评分");
        judgeResult.setStatus(JudgeStatusEnum.ACCEPTED);
        judgeResult.setActualOutput(aiResponse);
        judgeResult.setErrorMessage(feedback);
        judgeResult.setTimeUsed(score);
        judgeResult.setMemoryUsed(0);
        judgeResultMapper.insert(judgeResult);

        submission.setStatus(JudgeStatusEnum.ACCEPTED);
        submission.setTimeUsed(score);
        submissionMapper.updateById(submission);

        log.info("简答题判题完成: submissionId={}, score={}/10", submission.getId(), score);
    }

    private String fetchQuestionFromFeign(Long questionId) {
        try {
            Result<QuestionVO> result = questionFeignClient.getQuestionDetail(questionId);
            QuestionVO question = result != null ? result.getData() : null;
            if (question != null) {
                return objectMapper.writeValueAsString(question);
            }
        } catch (Exception e) {
            log.error("通过Feign获取题目失败: questionId={}", questionId, e);
        }
        return null;
    }
}
