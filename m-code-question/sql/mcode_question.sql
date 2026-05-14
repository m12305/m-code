-- =====================================================
-- m-code-question 题库服务 - 建库建表语句
-- =====================================================

CREATE DATABASE IF NOT EXISTS mcode_question
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE mcode_question;

-- 版块表
DROP TABLE IF EXISTS `question_section`;
CREATE TABLE `question_section` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '版块名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '版块描述',
    `icon`        VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `sort`        INT          DEFAULT 0  COMMENT '排序',
    `status`      TINYINT      DEFAULT 1  COMMENT '状态（1-启用 0-禁用）',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0  COMMENT '逻辑删除（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='版块表';

-- 题目分类表
DROP TABLE IF EXISTS `question_category`;
CREATE TABLE `question_category` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT       DEFAULT 0  COMMENT '父分类ID',
    `sort`        INT          DEFAULT 0  COMMENT '排序',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0  COMMENT '逻辑删除（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='题目分类表';

-- 标签表
DROP TABLE IF EXISTS `question_tag`;
CREATE TABLE `question_tag` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '标签名称',
    `color`       VARCHAR(20)  DEFAULT NULL COMMENT '标签颜色',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0  COMMENT '逻辑删除（0-未删除 1-已删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='标签表';

-- 题目表
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
    `id`               BIGINT       NOT NULL COMMENT '主键ID',
    `title`            VARCHAR(255) NOT NULL COMMENT '题目标题',
    `description`      TEXT         DEFAULT NULL COMMENT '题目描述',
    `type`             INT          NOT NULL DEFAULT 1 COMMENT '题目类型（1-编程题 2-选择题 3-简答题 4-判断题）',
    `section_id`       BIGINT       DEFAULT NULL COMMENT '所属版块ID',
    `template_code`    TEXT         DEFAULT NULL COMMENT '模板代码（编程题）',
    `test_cases`       TEXT         DEFAULT NULL COMMENT '测试用例JSON（编程题）',
    `options`          TEXT         DEFAULT NULL COMMENT '选项JSON（选择题/判断题）',
    `correct_answer`   VARCHAR(255) DEFAULT NULL COMMENT '正确答案（选择题/判断题）',
    `reference_answer` TEXT         DEFAULT NULL COMMENT '参考回答（简答题）',
    `difficulty`       INT          DEFAULT 1  COMMENT '难度（1-简单 2-中等 3-困难）',
    `category_id`      BIGINT       DEFAULT NULL COMMENT '分类ID',
    `accepted_count`   INT          DEFAULT 0  COMMENT '通过次数',
    `submission_count` INT          DEFAULT 0  COMMENT '提交次数',
    `status`           TINYINT      DEFAULT 1  COMMENT '状态（1-启用 0-禁用）',
    `create_time`      DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL COMMENT '更新时间',
    `deleted`          TINYINT      DEFAULT 0  COMMENT '逻辑删除（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_section_id` (`section_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='题目表';
