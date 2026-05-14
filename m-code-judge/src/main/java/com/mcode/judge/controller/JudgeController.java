package com.mcode.judge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.result.Result;
import com.mcode.judge.dto.SubmitCodeDTO;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/judge")
@RequiredArgsConstructor
public class JudgeController {

    private final JudgeService judgeService;

    @PostMapping("/submit")
    public Result<Submission> submit(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody SubmitCodeDTO dto) {
        return Result.ok(judgeService.submit(userId, dto));
    }

    @GetMapping("/submission")
    public Result<Page<Submission>> listSubmission(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.ok(judgeService.listSubmission(userId, pageNum, pageSize));
    }

    @GetMapping("/submission/{id}")
    public Result<Submission> getSubmission(@PathVariable Long id) {
        return Result.ok(judgeService.getSubmissionDetail(id));
    }

    @GetMapping("/result/{submissionId}")
    public Result<List<JudgeResult>> getResults(@PathVariable Long submissionId) {
        return Result.ok(judgeService.getJudgeResults(submissionId));
    }
}
