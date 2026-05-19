package com.mcode.exam.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcode.common.dto.SubmitCodeDTO;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.result.Result;
import com.mcode.exam.dto.ExamJudgeMessage;
import com.mcode.exam.dto.JudgeSubmissionVO;
import com.mcode.exam.entity.ExamAnswer;
import com.mcode.exam.entity.ExamQuestion;
import com.mcode.exam.feign.JudgeFeignClient;
import com.mcode.exam.mapper.ExamAnswerMapper;
import com.mcode.exam.mapper.ExamQuestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExamConsumer {

    private final ExamAnswerMapper examAnswerMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final JudgeFeignClient judgeFeignClient;

    @RabbitListener(queues = ExamRabbitMQConfig.EXAM_JUDGE_QUEUE)
    public void handleExamJudge(ExamJudgeMessage message) {
        Long examId = message.getExamId();
        Long userId = message.getUserId();
        log.info("收到异步判题任务: examId={}, userId={}", examId, userId);

        List<ExamAnswer> answers = examAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamAnswer>()
                        .eq(ExamAnswer::getExamId, examId)
                        .eq(ExamAnswer::getUserId, userId));
        if (answers.isEmpty()) {
            log.warn("未找到答题记录: examId={}, userId={}", examId, userId);
            return;
        }

        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));

        for (ExamAnswer answer : answers) {
            ExamQuestion eq = examQuestions.stream()
                    .filter(q -> q.getQuestionId().equals(answer.getQuestionId()))
                    .findFirst().orElse(null);

            SubmitCodeDTO submitDTO = new SubmitCodeDTO();
            submitDTO.setQuestionId(answer.getQuestionId());
            submitDTO.setAnswer(answer.getAnswer());
            submitDTO.setLanguage(answer.getLanguage());

            try {
                //todo JudgeSubmissionVO 和 submission 结构不一样
                Result<JudgeSubmissionVO> result = judgeFeignClient.submit(userId, submitDTO);
                JudgeSubmissionVO submission = result.getData();
                if (submission != null) {
                    answer.setSubmissionId(submission.getId());
                    answer.setStatus(submission.getStatus());
                    if (submission.getStatus() == JudgeStatusEnum.ACCEPTED && eq != null) {
                        answer.setScore(eq.getScore());
                    }
                    examAnswerMapper.updateById(answer);
                }
            } catch (Exception e) {
                log.error("异步判题失败: questionId={}", answer.getQuestionId(), e);
            }
        }

        log.info("异步判题完成: examId={}, userId={}", examId, userId);
    }
}
