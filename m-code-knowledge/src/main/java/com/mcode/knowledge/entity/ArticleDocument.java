package com.mcode.knowledge.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "articles")
public class ArticleDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Long)
    private Long authorId;

    @Field(type = FieldType.Keyword)
    private String authorName;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createTime;

    public static ArticleDocument fromEntity(Article article) {
        ArticleDocument doc = new ArticleDocument();
        doc.setId(article.getId());
        doc.setTitle(article.getTitle());
        doc.setSummary(article.getSummary());
        doc.setContent(article.getContent());
        doc.setCategoryId(article.getCategoryId());
        doc.setAuthorId(article.getAuthorId());
        doc.setAuthorName(article.getAuthorName());
        doc.setViewCount(article.getViewCount() != null ? article.getViewCount() : 0);
        doc.setLikeCount(article.getLikeCount() != null ? article.getLikeCount() : 0);
        doc.setStatus(article.getStatus());
        doc.setCreateTime(article.getCreateTime());
        return doc;
    }
}
