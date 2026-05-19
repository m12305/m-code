package com.mcode.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.result.Result;
import com.mcode.exam.dto.CreateExamDTO;
import com.mcode.exam.dto.SubmitExamDTO;
import com.mcode.exam.entity.Exam;
import com.mcode.exam.entity.ExamQuestion;
import com.mcode.exam.entity.ExamRecord;
import com.mcode.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/list")
    public Result<Page<Exam>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.ok(examService.pageExam(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Exam> detail(@PathVariable Long id) {
        return Result.ok(examService.getExamDetail(id));
    }

    @GetMapping("/{id}/questions")
    public Result<List<ExamQuestion>> questions(@PathVariable Long id) {
        return Result.ok(examService.getExamQuestions(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody CreateExamDTO dto) {
        examService.addExam(dto);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Exam exam) {
        examService.updateExam(exam);
        return Result.ok();
    }

    @PostMapping("/{examId}/start")
    public Result<String> start(@PathVariable Long examId,
                               @RequestHeader("X-User-Id") Long userId) {
        examService.startExam(examId, userId);
        return Result.ok("已开始考试");
    }

    @PostMapping("/{examId}/submit")
    public Result<String> submit(@PathVariable Long examId,
                                @RequestHeader("X-User-Id") Long userId,
                                @RequestBody SubmitExamDTO dto) {
        examService.submitExam(examId, userId, dto);
        return Result.ok("已交卷");
    }

    @GetMapping("/my-records")
    public Result<List<ExamRecord>> myRecords(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(examService.getMyRecords(userId));
    }

    @GetMapping("/{examId}/my-record")
    public Result<ExamRecord> myRecord(@PathVariable Long examId,
                                        @RequestHeader("X-User-Id") Long userId) {
        return Result.ok(examService.getMyExamRecord(examId, userId));
    }

    @GetMapping("/{examId}/rank")
    public Result<List<ExamRecord>> rank(@PathVariable Long examId) {
        return Result.ok(examService.getExamRank(examId));
    }
}
