package com.mcode.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.knowledge.entity.Article;
import com.mcode.knowledge.entity.KnowledgeCategory;
import com.mcode.knowledge.entity.LearningPath;
import com.mcode.knowledge.mapper.ArticleMapper;
import com.mcode.knowledge.mapper.KnowledgeCategoryMapper;
import com.mcode.knowledge.mapper.LearningPathMapper;
import com.mcode.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final LearningPathMapper learningPathMapper;

    @Override
    public List<KnowledgeCategory> listCategory() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>().orderByAsc(KnowledgeCategory::getSort));
    }

    @Override
    public void addCategory(KnowledgeCategory category) {
        categoryMapper.insert(category);
    }

    @Override
    public Page<Article> pageArticle(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreateTime);
        return articleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return article;
    }

    @Override
    public void addArticle(Article article) {
        articleMapper.insert(article);
    }

    @Override
    public void updateArticle(Article article) {
        articleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public List<LearningPath> listLearningPath() {
        return learningPathMapper.selectList(
                new LambdaQueryWrapper<LearningPath>().orderByAsc(LearningPath::getSort));
    }

    @Override
    public LearningPath getLearningPathDetail(Long id) {
        LearningPath path = learningPathMapper.selectById(id);
        if (path == null) {
            throw new BusinessException("学习路径不存在");
        }
        return path;
    }

    @Override
    public void addLearningPath(LearningPath path) {
        learningPathMapper.insert(path);
    }
}
