package com.mcode.judge.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.common.exception.BusinessException;
import com.mcode.judge.dto.SubmitCodeDTO;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.mapper.JudgeResultMapper;
import com.mcode.judge.mapper.SubmissionMapper;
import com.mcode.judge.service.JudgeService;
import com.mcode.judge.strategy.JudgeStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private static final String CACHE_KEY_PREFIX = "question:detail:";

    private final SubmissionMapper submissionMapper;
    private final JudgeResultMapper judgeResultMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final JudgeStrategyFactory strategyFactory;

    @Override
    public Submission submit(Long userId, SubmitCodeDTO dto) {
        String cacheKey = CACHE_KEY_PREFIX + dto.getQuestionId();
        String questionJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(questionJson)) {
            throw new BusinessException("题目缓存不存在，请先查看题目详情");
        }

        QuestionTypeEnum type = parseType(questionJson);
        return strategyFactory.getStrategy(type)
                .judge(userId, dto.getQuestionId(), dto.getAnswer(), dto.getLanguage(), questionJson);
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

    private QuestionTypeEnum parseType(String questionJson) {
        try {
            JsonNode node = objectMapper.readTree(questionJson);
            int typeCode = node.get("type").asInt();
            for (QuestionTypeEnum t : QuestionTypeEnum.values()) {
                if (t.getCode() == typeCode) {
                    return t;
                }
            }
        } catch (Exception e) {
            log.error("解析题目类型失败", e);
        }
        throw new BusinessException("无法识别题目类型");
    }
}
