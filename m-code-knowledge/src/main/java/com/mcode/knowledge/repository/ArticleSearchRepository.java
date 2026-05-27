package com.mcode.knowledge.repository;

import com.mcode.knowledge.entity.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleSearchRepository extends ElasticsearchRepository<ArticleDocument, Long> {
}
