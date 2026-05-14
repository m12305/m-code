-- =====================================================
-- m-code-user 用户服务 - 建库建表语句
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mcode
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE mcode;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL COMMENT '主键ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（bcrypt加密）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `score`       INT          DEFAULT 0  COMMENT '积分',
    `status`      TINYINT      DEFAULT 1  COMMENT '状态（1-正常 0-禁用）',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0  COMMENT '逻辑删除（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- 给现有 user 表增加 role 字段（如已建表则执行此句）
ALTER TABLE `user` ADD COLUMN `role` TINYINT DEFAULT 0 COMMENT '角色（0-普通用户 1-管理员）' AFTER `status`;
