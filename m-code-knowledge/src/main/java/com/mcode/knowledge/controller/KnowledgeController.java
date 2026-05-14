package com.mcode.knowledge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.result.Result;
import com.mcode.knowledge.entity.Article;
import com.mcode.knowledge.entity.KnowledgeCategory;
import com.mcode.knowledge.entity.LearningPath;
import com.mcode.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping("/category")
    public Result<List<KnowledgeCategory>> listCategory() {
        return Result.ok(knowledgeService.listCategory());
    }

    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody KnowledgeCategory category) {
        knowledgeService.addCategory(category);
        return Result.ok();
    }

    @GetMapping("/article")
    public Result<Page<Article>> listArticle(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "20") Integer pageSize,
                                              @RequestParam(required = false) Long categoryId) {
        return Result.ok(knowledgeService.pageArticle(pageNum, pageSize, categoryId));
    }

    @GetMapping("/article/{id}")
    public Result<Article> getArticle(@PathVariable Long id) {
        return Result.ok(knowledgeService.getArticleDetail(id));
    }

    @PostMapping("/article")
    public Result<Void> addArticle(@RequestBody Article article) {
        knowledgeService.addArticle(article);
        return Result.ok();
    }

    @PutMapping("/article")
    public Result<Void> updateArticle(@RequestBody Article article) {
        knowledgeService.updateArticle(article);
        return Result.ok();
    }

    @DeleteMapping("/article/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        knowledgeService.deleteArticle(id);
        return Result.ok();
    }

    @GetMapping("/path")
    public Result<List<LearningPath>> listLearningPath() {
        return Result.ok(knowledgeService.listLearningPath());
    }

    @GetMapping("/path/{id}")
    public Result<LearningPath> getLearningPath(@PathVariable Long id) {
        return Result.ok(knowledgeService.getLearningPathDetail(id));
    }

    @PostMapping("/path")
    public Result<Void> addLearningPath(@RequestBody LearningPath path) {
        knowledgeService.addLearningPath(path);
        return Result.ok();
    }
}
