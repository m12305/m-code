package com.mcode.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.common.result.Result;
import com.mcode.question.dto.QuestionAddDTO;
import com.mcode.question.entity.Category;
import com.mcode.question.entity.Question;
import com.mcode.question.entity.Section;
import com.mcode.question.entity.Tag;
import com.mcode.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 分页查询题目列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @param categoryId 分类id
     * @param difficulty 难度
     * @param type 题目类型
     * @param sectionId 板块id
     */
    @GetMapping("/list")
    public Result<Page<Question>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Integer difficulty,
                                       @RequestParam(required = false) Integer type,
                                       @RequestParam(required = false) Long sectionId) {
        return Result.ok(questionService.pageQuestion(pageNum, pageSize, categoryId, difficulty, type, sectionId));
    }

    /**
     * 获取题目详情
     * @param id 题目id
     */
    @GetMapping("/detail/{id}")
    public Result<Question> detail(@PathVariable Long id) {
        return Result.ok(questionService.getQuestionDetail(id));
    }

    /**
     * 添加题目，应该进行参数校验
     * @param dto 题目信息
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody QuestionAddDTO dto) {
        questionService.addQuestion(dto);
        return Result.ok();
    }

    /**
     * 修改题目
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Question question) {
        questionService.updateQuestion(question);
        return Result.ok();
    }

    /**
     * 删除题目
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.ok();
    }


    @GetMapping("/category")
    public Result<List<Category>> listCategory() {
        return Result.ok(questionService.listCategory());
    }

    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody Category category) {
        questionService.addCategory(category);
        return Result.ok();
    }

    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody Category category) {
        questionService.updateCategory(category);
        return Result.ok();
    }

    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        questionService.deleteCategory(id);
        return Result.ok();
    }


    @GetMapping("/tag")
    public Result<List<Tag>> listTag() {
        return Result.ok(questionService.listTag());
    }

    @PostMapping("/tag")
    public Result<Void> addTag(@RequestBody Tag tag) {
        questionService.addTag(tag);
        return Result.ok();
    }

    @PutMapping("/tag")
    public Result<Void> updateTag(@RequestBody Tag tag) {
        questionService.updateTag(tag);
        return Result.ok();
    }

    @DeleteMapping("/tag/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        questionService.deleteTag(id);
        return Result.ok();
    }


    /**
     * 获取板块列表
     * @return 板块列表
     */
    @GetMapping("/section")
    public Result<List<Section>> listSection() {
        return Result.ok(questionService.listSection());
    }

    /**
     * 添加板块
     * @param section 板块信息
     * @param role 权限验证，仅管理员可进行操作
     */
    @PostMapping("/section")
    public Result<Void> addSection(@RequestBody Section section,
                                   @RequestHeader(value = "X-User-Role", defaultValue = "0") String role) {
        checkAdmin(role);
        questionService.addSection(section);
        return Result.ok();
    }

    /**
     * 更新板块
     * @param section 板块信息
     * @param role 权限验证，仅管理员可进行操作
     */
    @PutMapping("/section")
    public Result<Void> updateSection(@RequestBody Section section,
                                      @RequestHeader(value = "X-User-Role", defaultValue = "0") String role) {
        checkAdmin(role);
        questionService.updateSection(section);
        return Result.ok();
    }

    /**
     * 删除板块
     * @param id 板块id
     * @param role 权限验证，仅管理员可进行操作
     */
    @DeleteMapping("/section/{id}")
    public Result<Void> deleteSection(@PathVariable Long id,
                                      @RequestHeader(value = "X-User-Role", defaultValue = "0") String role) {
        checkAdmin(role);
        questionService.deleteSection(id);
        return Result.ok();
    }

    /**
     * 管理员权限验证，从请求头中拿到role信息进行权限比对
     * @param role
     */
    private void checkAdmin(String role) {
        if (!"1".equals(role)) {
            throw new BusinessException(403, "暂无操作权限，仅管理员可执行此操作");
        }
    }
}
