package com.mcode.question.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.exception.BusinessException;
import com.mcode.question.entity.Category;
import com.mcode.question.entity.Question;
import com.mcode.question.entity.Section;
import com.mcode.question.entity.Tag;
import com.mcode.question.mapper.CategoryMapper;
import com.mcode.question.mapper.QuestionMapper;
import com.mcode.question.mapper.SectionMapper;
import com.mcode.question.mapper.TagMapper;
import com.mcode.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final String CACHE_KEY_PREFIX = "question:detail:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final QuestionMapper questionMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final SectionMapper sectionMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Page<Question> pageQuestion(Integer pageNum, Integer pageSize, Long categoryId, Integer difficulty, Integer type, Long sectionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Question::getCategoryId, categoryId);
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);
        wrapper.eq(type != null, Question::getType, type);
        wrapper.eq(sectionId != null, Question::getSectionId, sectionId);
        wrapper.orderByDesc(Question::getCreateTime);
        return questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Question getQuestionDetail(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(cached)) {
            try {
                return objectMapper.readValue(cached, Question.class);
            } catch (Exception e) {
                log.warn("缓存反序列化失败: key={}", cacheKey, e);
            }
        }

        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(question), CACHE_TTL);
        } catch (Exception e) {
            log.warn("缓存写入失败: key={}", cacheKey, e);
        }
        return question;
    }

    private void evictCache(Long questionId) {
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + questionId);
    }

    @Override
    public void addQuestion(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
        evictCache(question.getId());
    }

    @Override
    public void deleteQuestion(Long id) {
        questionMapper.deleteById(id);
        evictCache(id);
    }

    @Override
    public List<Category> listCategory() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    @Override
    public void addCategory(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Tag> listTag() {
        return tagMapper.selectList(null);
    }

    @Override
    public void addTag(Tag tag) {
        tagMapper.insert(tag);
    }

    @Override
    public void updateTag(Tag tag) {
        tagMapper.updateById(tag);
    }

    @Override
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    public List<Section> listSection() {
        return sectionMapper.selectList(new LambdaQueryWrapper<Section>().orderByAsc(Section::getSort));
    }

    @Override
    public void addSection(Section section) {
        sectionMapper.insert(section);
    }

    @Override
    public void updateSection(Section section) {
        sectionMapper.updateById(section);
    }

    @Override
    public void deleteSection(Long id) {
        Long count = questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getSectionId, id));
        if (count > 0) {
            throw new BusinessException("版块下存在题目，无法删除");
        }
        sectionMapper.deleteById(id);
    }
}
