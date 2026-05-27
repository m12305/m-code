package com.mcode.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.exam.dto.CreateExamDTO;
import com.mcode.exam.dto.SubmitExamDTO;
import com.mcode.exam.entity.Exam;
import com.mcode.exam.entity.ExamQuestion;
import com.mcode.exam.entity.ExamRecord;

import java.util.List;

public interface ExamService {
    Page<Exam> pageExam(Integer pageNum, Integer pageSize);
    Exam getExamDetail(Long id);
    List<ExamQuestion> getExamQuestions(Long examId);
    void addExam(CreateExamDTO dto);
    void updateExam(CreateExamDTO dto);
    void deleteExam(Long id);

    void startExam(Long examId, Long userId);
    void submitExam(Long examId, Long userId, SubmitExamDTO dto);
    ExamRecord getMyExamRecord(Long examId, Long userId);

    List<ExamRecord> getMyRecords(Long userId);
    List<ExamRecord> getExamRank(Long examId);
}
