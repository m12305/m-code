package com.mcode.judge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.result.Result;
import com.mcode.common.dto.SubmitCodeDTO;
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

    //提交答案，判题
    @PostMapping("/submit")
    public Result<Submission> submit(@RequestHeader("X-User-Id") Long userId,
                                      @RequestBody SubmitCodeDTO dto) {
        return Result.ok(judgeService.submit(userId, dto));
    }

    //查看提交记录
    @GetMapping("/submission")
    public Result<Page<Submission>> listSubmission(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.ok(judgeService.listSubmission(userId, pageNum, pageSize));
    }

    //获取某条记录的详情
    @GetMapping("/submission/{id}")
    public Result<Submission> getSubmission(@PathVariable Long id) {
        return Result.ok(judgeService.getSubmissionDetail(id));
    }

    //获取提交结果
    @GetMapping("/result/{submissionId}")
    public Result<List<JudgeResult>> getResults(@PathVariable Long submissionId) {
        return Result.ok(judgeService.getJudgeResults(submissionId));
    }
}
