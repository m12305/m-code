package com.mcode.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.exam.entity.Exam;
import com.mcode.exam.entity.ExamRecord;

import java.util.List;

public interface ExamService {
    Page<Exam> pageExam(Integer pageNum, Integer pageSize);
    Exam getExamDetail(Long id);
    void addExam(Exam exam);
    void updateExam(Exam exam);

    void startExam(Long examId, Long userId);
    void submitExam(Long examId, Long userId);

    List<ExamRecord> getMyRecords(Long userId);
    List<ExamRecord> getExamRank(Long examId);
}
