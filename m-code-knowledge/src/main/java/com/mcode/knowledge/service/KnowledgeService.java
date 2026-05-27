package com.mcode.knowledge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.knowledge.entity.Article;
import com.mcode.knowledge.entity.KnowledgeCategory;
import com.mcode.knowledge.entity.LearningPath;

import java.util.List;

public interface KnowledgeService {
    List<KnowledgeCategory> listCategory();
    void addCategory(KnowledgeCategory category);

    Page<Article> pageArticle(Integer pageNum, Integer pageSize, Long categoryId);
    Page<Article> searchArticle(String keyword, Integer pageNum, Integer pageSize);
    Article getArticleDetail(Long id);
    void addArticle(Article article);
    void updateArticle(Article article);
    void deleteArticle(Long id);

    List<LearningPath> listLearningPath();
    LearningPath getLearningPathDetail(Long id);
    void addLearningPath(LearningPath path);
}
