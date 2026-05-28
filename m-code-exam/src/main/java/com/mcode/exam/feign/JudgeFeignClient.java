package com.mcode.exam.feign;

import com.mcode.common.dto.SubmitCodeDTO;
import com.mcode.common.result.Result;
import com.mcode.exam.dto.JudgeResultVO;
import com.mcode.exam.dto.JudgeSubmissionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "m-code-judge")
public interface JudgeFeignClient {

    @PostMapping("/api/judge/submit")
    Result<JudgeSubmissionVO> submit(@RequestHeader("X-User-Id") Long userId,
                                     @RequestBody SubmitCodeDTO dto);

    @GetMapping("/api/judge/submission/{id}")
    Result<JudgeSubmissionVO> getSubmission(@PathVariable("id") Long id);

    @GetMapping("/api/judge/result/{submissionId}")
    Result<List<JudgeResultVO>> getResults(@PathVariable("submissionId") Long submissionId);
}
