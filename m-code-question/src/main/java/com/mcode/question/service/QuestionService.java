package com.mcode.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.question.entity.Category;
import com.mcode.question.entity.Question;
import com.mcode.question.entity.Section;
import com.mcode.question.entity.Tag;

import java.util.List;

public interface QuestionService {
    Page<Question> pageQuestion(Integer pageNum, Integer pageSize, Long categoryId, Integer difficulty, Integer type, Long sectionId);
    Question getQuestionDetail(Long id);
    void addQuestion(Question question);
    void updateQuestion(Question question);
    void deleteQuestion(Long id);

    List<Category> listCategory();
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Long id);

    List<Tag> listTag();
    void addTag(Tag tag);
    void updateTag(Tag tag);
    void deleteTag(Long id);

    List<Section> listSection();
    void addSection(Section section);
    void updateSection(Section section);
    void deleteSection(Long id);
}
