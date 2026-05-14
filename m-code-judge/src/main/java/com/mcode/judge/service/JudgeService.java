package com.mcode.judge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.judge.dto.SubmitCodeDTO;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;

import java.util.List;

public interface JudgeService {
    Submission submit(Long userId, SubmitCodeDTO dto);
    Page<Submission> listSubmission(Long userId, Integer pageNum, Integer pageSize);
    Submission getSubmissionDetail(Long id);
    List<JudgeResult> getJudgeResults(Long submissionId);
}
