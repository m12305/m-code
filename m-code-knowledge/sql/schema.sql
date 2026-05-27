-- m-code-knowledge 模块建表语句

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mcode_knowledge
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE mcode_knowledge;

-- 知识分类表
CREATE TABLE knowledge_category (
    id BIGINT NOT NULL COMMENT '主键',
    name VARCHAR(128) NOT NULL COMMENT '分类名称',
    icon VARCHAR(255) COMMENT '图标',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识分类表';

-- 文章表
CREATE TABLE article (
    id BIGINT NOT NULL COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    summary VARCHAR(512) COMMENT '文章摘要',
    content LONGTEXT COMMENT '文章内容（Markdown）',
    category_id BIGINT COMMENT '分类ID',
    author_id BIGINT COMMENT '作者用户ID',
    author_name VARCHAR(64) COMMENT '作者名称',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status INT DEFAULT 1 COMMENT '状态 1=已发布 0=草稿',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_category_id (category_id),
    INDEX idx_author_id (author_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章表';

-- 学习路线表
CREATE TABLE learning_path (
    id BIGINT NOT NULL COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '路线标题',
    description TEXT COMMENT '路线描述',
    cover_image VARCHAR(512) COMMENT '封面图片URL',
    article_ids VARCHAR(2048) COMMENT '关联文章ID列表，逗号分隔',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1=已发布 0=草稿',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_status (status),
    INDEX idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学习路线表';
