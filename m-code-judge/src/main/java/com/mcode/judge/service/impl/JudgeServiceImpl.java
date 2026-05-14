package com.mcode.judge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.enums.LanguageEnum;
import com.mcode.common.exception.BusinessException;
import com.mcode.judge.dto.SubmitCodeDTO;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.JudgeResultMapper;
import com.mcode.judge.mapper.SubmissionMapper;
import com.mcode.judge.service.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final SubmissionMapper submissionMapper;
    private final JudgeResultMapper judgeResultMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Submission submit(Long userId, SubmitCodeDTO dto) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(dto.getQuestionId());
        submission.setCode(dto.getCode());
        submission.setLanguage(LanguageEnum.values()[dto.getLanguage() - 1]);
        submission.setStatus(JudgeStatusEnum.PENDING);
        submissionMapper.insert(submission);

        rabbitTemplate.convertAndSend("judge.exchange", "judge.submit", submission.getId());
        return submission;
    }

    @Override
    public Page<Submission> listSubmission(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<Submission>()
                .eq(Submission::getUserId, userId)
                .orderByDesc(Submission::getCreateTime);
        return submissionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Submission getSubmissionDetail(Long id) {
        Submission submission = submissionMapper.selectById(id);
        if (submission == null) {
            throw new BusinessException("提交记录不存在");
        }
        return submission;
    }

    @Override
    public List<JudgeResult> getJudgeResults(Long submissionId) {
        return judgeResultMapper.selectList(
                new LambdaQueryWrapper<JudgeResult>().eq(JudgeResult::getSubmissionId, submissionId));
    }
}
