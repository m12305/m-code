package com.mcode.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.exam.entity.Exam;
import com.mcode.exam.entity.ExamRecord;
import com.mcode.exam.mapper.ExamMapper;
import com.mcode.exam.mapper.ExamRecordMapper;
import com.mcode.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;

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
    public void addExam(Exam exam) {
        examMapper.insert(exam);
    }

    @Override
    public void updateExam(Exam exam) {
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
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setUserId(userId);
        record.setStatus(1);
        examRecordMapper.insert(record);
    }

    @Override
    public void submitExam(Long examId, Long userId) {
        ExamRecord record = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getExamId, examId)
                        .eq(ExamRecord::getUserId, userId)
                        .eq(ExamRecord::getStatus, 1));
        if (record == null) {
            throw new BusinessException("未找到考试记录");
        }
        record.setStatus(2);
        record.setSubmitTime(LocalDateTime.now());
        examRecordMapper.updateById(record);
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
}
