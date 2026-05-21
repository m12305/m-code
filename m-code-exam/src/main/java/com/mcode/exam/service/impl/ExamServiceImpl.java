package com.mcode.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.exception.BusinessException;
import com.mcode.common.result.Result;
import com.mcode.exam.dto.CreateExamDTO;
import com.mcode.exam.dto.ExamJudgeMessage;
import com.mcode.exam.dto.JudgeSubmissionVO;
import com.mcode.exam.dto.SubmitExamDTO;
import com.mcode.exam.entity.Exam;
import com.mcode.exam.entity.ExamAnswer;
import com.mcode.exam.entity.ExamQuestion;
import com.mcode.exam.entity.ExamRecord;
import com.mcode.exam.feign.JudgeFeignClient;
import com.mcode.exam.mapper.ExamAnswerMapper;
import com.mcode.exam.mapper.ExamMapper;
import com.mcode.exam.mapper.ExamQuestionMapper;
import com.mcode.exam.mapper.ExamRecordMapper;
import com.mcode.exam.mq.ExamRabbitMQConfig;
import com.mcode.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final ExamQuestionMapper examQuestionMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final JudgeFeignClient judgeFeignClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Page<Exam> pageExam(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<Exam>()
                .orderByDesc(Exam::getCreateTime);
        return examMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Exam getExamDetail(Long id) {
        Exam exam = examMapper.selectById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        return exam;
    }

    @Override
    public List<ExamQuestion> getExamQuestions(Long examId) {
        return examQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamQuestion>()
                        .eq(ExamQuestion::getExamId, examId)
                        .orderByAsc(ExamQuestion::getSort));
    }

    @Override
    public void addExam(CreateExamDTO dto) {
        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setTotalScore(dto.getQuestions().stream()
                .mapToInt(CreateExamDTO.ExamQuestionDTO::getScore).sum());
        exam.setStatus(1);
        examMapper.insert(exam);

        for (CreateExamDTO.ExamQuestionDTO q : dto.getQuestions()) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(q.getQuestionId());
            eq.setScore(q.getScore());
            eq.setSort(q.getSort());
            examQuestionMapper.insert(eq);
        }
    }

    @Override
    public void updateExam(CreateExamDTO dto) {
        Exam exam = examMapper.selectById(dto.getId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        exam.setTitle(dto.getTitle());
        exam.setDescription(dto.getDescription());
        exam.setDuration(dto.getDuration());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());

        if (dto.getQuestions() != null) {
            exam.setTotalScore(dto.getQuestions().stream()
                    .mapToInt(CreateExamDTO.ExamQuestionDTO::getScore).sum());

            examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                    .eq(ExamQuestion::getExamId, dto.getId()));

            int sort = 1;
            for (CreateExamDTO.ExamQuestionDTO q : dto.getQuestions()) {
                ExamQuestion eq = new ExamQuestion();
                eq.setExamId(dto.getId());
                eq.setQuestionId(q.getQuestionId());
                eq.setScore(q.getScore());
                eq.setSort(q.getSort() != null ? q.getSort() : sort++);
                examQuestionMapper.insert(eq);
            }
        }

        examMapper.updateById(exam);
    }

    @Override
    public void startExam(Long examId, Long userId) {
        Exam exam = getExamDetail(examId);
        if (exam.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("考试尚未开始");
        }
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("考试已结束");
        }

        ExamRecord existing = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
                        .eq(ExamRecord::getUserId, userId));
        if (existing != null) {
            if (existing.getStatus() != null && existing.getStatus() != 1) {
                throw new BusinessException("您已完成该考试，不可重复参加");
            }
            return;
        }

        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setUserId(userId);
        record.setStatus(1);
        examRecordMapper.insert(record);
    }

    @Override
    public void submitExam(Long examId, Long userId, SubmitExamDTO dto) {
        ExamRecord record = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
                        .eq(ExamRecord::getUserId, userId)
                        .eq(ExamRecord::getStatus, 1));
        if (record == null) {
            throw new BusinessException("未找到考试记录");
        }

        List<ExamQuestion> examQuestions = examQuestionMapper.selectList(
                new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));

        for (SubmitExamDTO.AnswerDTO answerDTO : dto.getAnswers()) {
            ExamQuestion eq = examQuestions.stream()
                    .filter(q -> q.getQuestionId().equals(answerDTO.getQuestionId()))
                    .findFirst().orElse(null);
            if (eq == null) {
                continue;
            }

            ExamAnswer examAnswer = new ExamAnswer();
            examAnswer.setExamId(examId);
            examAnswer.setUserId(userId);
            examAnswer.setQuestionId(answerDTO.getQuestionId());
            examAnswer.setAnswer(answerDTO.getAnswer());
            examAnswer.setLanguage(answerDTO.getLanguage());
            examAnswer.setStatus(JudgeStatusEnum.PENDING);
            examAnswer.setScore(0);
            examAnswerMapper.insert(examAnswer);
        }

        record.setStatus(4);
        record.setSubmitTime(LocalDateTime.now());
        record.setTotalScore(0);
        examRecordMapper.updateById(record);

        rabbitTemplate.convertAndSend(ExamRabbitMQConfig.EXAM_JUDGE_EXCHANGE,
                ExamRabbitMQConfig.EXAM_JUDGE_ROUTING_KEY,
                new ExamJudgeMessage(examId, userId));

        log.info("交卷完成: examId={}, userId={}", examId, userId);
    }

    @Override
    public List<ExamRecord> getMyRecords(Long userId) {
        return examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getUserId, userId)
                        .orderByDesc(ExamRecord::getCreateTime));
    }

    @Override
    public List<ExamRecord> getExamRank(Long examId) {
        return examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
                        .orderByDesc(ExamRecord::getTotalScore));
    }

    @Override
    public ExamRecord getMyExamRecord(Long examId, Long userId) {
        ExamRecord record = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
                        .eq(ExamRecord::getUserId, userId));
        if (record == null) {
            throw new BusinessException("未找到考试记录");
        }
        if (record.getStatus() == 4 || record.getStatus() == 3) {
            refreshExamRecord(record);
        }
        return record;
    }

    private void refreshExamRecord(ExamRecord record) {
        List<ExamAnswer> answers = examAnswerMapper.selectList(
                new LambdaQueryWrapper<ExamAnswer>()
                        .eq(ExamAnswer::getExamId, record.getExamId())
                        .eq(ExamAnswer::getUserId, record.getUserId()));

        boolean hasPendingOrRunning = false;
        int totalScore = 0;

        for (ExamAnswer answer : answers) {
            JudgeStatusEnum status = answer.getStatus();
            if (status == JudgeStatusEnum.PENDING || status == JudgeStatusEnum.RUNNING) {
                if (answer.getSubmissionId() != null) {
                    try {
                        //todo JudgeSubmissionVO 和 submission 结构不一样
                        Result<JudgeSubmissionVO> result = judgeFeignClient.getSubmission(answer.getSubmissionId());
                        JudgeSubmissionVO sub = result.getData();
                        if (sub != null && sub.getStatus() != null) {
                            status = sub.getStatus();
                            answer.setStatus(status);
                            examAnswerMapper.updateById(answer);
                        }
                    } catch (Exception e) {
                        log.error("获取判题状态失败: submissionId={}", answer.getSubmissionId(), e);
                    }
                }
            }

            if (status == JudgeStatusEnum.PENDING || status == JudgeStatusEnum.RUNNING) {
                hasPendingOrRunning = true;
            } else if (status == JudgeStatusEnum.ACCEPTED) {
                ExamQuestion eq = examQuestionMapper.selectOne(
                        new LambdaQueryWrapper<ExamQuestion>()
                                .eq(ExamQuestion::getExamId, record.getExamId())
                                .eq(ExamQuestion::getQuestionId, answer.getQuestionId()));
                int questionScore = eq != null ? eq.getScore() : 0;
                answer.setScore(questionScore);
                examAnswerMapper.updateById(answer);
                totalScore += questionScore;
            }
        }

        if (!hasPendingOrRunning) {
            record.setStatus(2);
            record.setTotalScore(totalScore);
            examRecordMapper.updateById(record);
        } else if (record.getStatus() != 3) {
            record.setStatus(3);
            examRecordMapper.updateById(record);
        }
    }
}
