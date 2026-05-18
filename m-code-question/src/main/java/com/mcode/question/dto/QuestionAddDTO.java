package com.mcode.question.dto;

import lombok.Data;

/**
 * 接收前端新增题目参数的 DTO
 */
@Data
public class QuestionAddDTO {

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目描述
     */
    private String description;

    /**
     * 代码模板
     */
    private String templateCode;

    /**
     * 测试用例
     */
    private String testCases;

    /**
     * 难度：EASY/MEDIUM/HARD（前端传字符串）
     */
    private String difficulty;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 题目类型：单选/多选/判断/编程（传字符串）
     */
    private String type;

    /**
     * 选项（JSON字符串）
     */
    private String options;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 参考答案/解析
     */
    private String referenceAnswer;


}
