package com.mcode.knowledge.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.knowledge.entity.Article;
import com.mcode.knowledge.entity.ArticleDocument;
import com.mcode.knowledge.entity.KnowledgeCategory;
import com.mcode.knowledge.entity.LearningPath;
import com.mcode.knowledge.mapper.ArticleMapper;
import com.mcode.knowledge.mapper.KnowledgeCategoryMapper;
import com.mcode.knowledge.mapper.LearningPathMapper;
import com.mcode.knowledge.repository.ArticleSearchRepository;
import com.mcode.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeCategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final LearningPathMapper learningPathMapper;
    private final ArticleSearchRepository articleSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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
    public Page<Article> searchArticle(String keyword, Integer pageNum, Integer pageSize) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q
                        .multiMatch(MultiMatchQuery.of(m -> m
                                .fields("title", "summary", "content")
                                .query(keyword)
                        ))
                ))
                .withPageable(org.springframework.data.domain.PageRequest.of(pageNum - 1, pageSize))
                .build();

        var searchHits = elasticsearchOperations.search(
                query, ArticleDocument.class, elasticsearchOperations.getIndexCoordinatesFor(ArticleDocument.class));

        List<Article> articles = searchHits.stream()
                .map(hit -> {
                    ArticleDocument doc = hit.getContent();
                    Article article = new Article();
                    article.setId(doc.getId());
                    article.setTitle(doc.getTitle());
                    article.setSummary(doc.getSummary());
                    article.setContent(doc.getContent());
                    article.setCategoryId(doc.getCategoryId());
                    article.setAuthorId(doc.getAuthorId());
                    article.setAuthorName(doc.getAuthorName());
                    article.setViewCount(doc.getViewCount());
                    article.setLikeCount(doc.getLikeCount());
                    article.setStatus(doc.getStatus());
                    article.setCreateTime(doc.getCreateTime());
                    return article;
                }).toList();

        Page<Article> page = new Page<>(pageNum, pageSize);
        page.setTotal(searchHits.getTotalHits());
        page.setRecords(articles);
        return page;
    }

    @Override
    public void addArticle(Article article) {
        articleMapper.insert(article);
        syncToEs(article);
    }

    @Override
    public void updateArticle(Article article) {
        articleMapper.updateById(article);
        Article latest = articleMapper.selectById(article.getId());
        if (latest != null) {
            syncToEs(latest);
        }
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
        try {
            articleSearchRepository.deleteById(id);
        } catch (Exception e) {
            log.error("ES文章删除失败: id={}", id, e);
        }
    }

    private void syncToEs(Article article) {
        try {
            articleSearchRepository.save(ArticleDocument.fromEntity(article));
        } catch (Exception e) {
            log.error("ES文章同步失败: id={}", article.getId(), e);
        }
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
