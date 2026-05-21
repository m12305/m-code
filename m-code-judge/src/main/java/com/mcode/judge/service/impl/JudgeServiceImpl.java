package com.mcode.judge.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.common.exception.BusinessException;
import com.mcode.common.dto.QuestionVO;
import com.mcode.common.dto.SubmitCodeDTO;
import com.mcode.common.result.Result;
import com.mcode.judge.entity.JudgeResult;
import com.mcode.judge.entity.Submission;
import com.mcode.judge.feign.QuestionFeignClient;
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
    private final QuestionFeignClient questionFeignClient;

    @Override
    public Submission submit(Long userId, SubmitCodeDTO dto) {
        String cacheKey = CACHE_KEY_PREFIX + dto.getQuestionId();
        String questionJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(questionJson)) {
            questionJson = fetchQuestionAndCache(dto.getQuestionId(), cacheKey);
        }

        QuestionTypeEnum type = parseType(questionJson);
        return strategyFactory.getStrategy(type)
                .judge(userId, dto.getQuestionId(), dto.getAnswer(), dto.getLanguage(), questionJson);
    }

    private String fetchQuestionAndCache(Long questionId, String cacheKey) {
        try {
            Result<QuestionVO> result = questionFeignClient.getQuestionDetail(questionId);
            QuestionVO question = result != null ? result.getData() : null;
            if (question == null) {
                throw new BusinessException("题目不存在");
            }
            String json = objectMapper.writeValueAsString(question);
            //stringRedisTemplate.opsForValue().set(cacheKey, json);
            return json;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("通过Feign获取题目失败: questionId={}", questionId, e);
            throw new BusinessException("题目不存在");
        }
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


    //解析题目类型
    private QuestionTypeEnum parseType(String questionJson) {
        try {
            JsonNode node = objectMapper.readTree(questionJson);
            String typeCode = node.get("type").asText();
            for (QuestionTypeEnum t : QuestionTypeEnum.values()) {
                if (t.toString().equals(typeCode)) {
                    return t;
                }
            }
        } catch (Exception e) {
            log.error("解析题目类型失败", e);
        }
        throw new BusinessException("无法识别题目类型");
    }
}
