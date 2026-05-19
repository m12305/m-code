-- m-code-exam 模块建表语句

CREATE TABLE exam (
    id BIGINT NOT NULL COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '考试标题',
    description TEXT COMMENT '考试描述',
    duration INT COMMENT '考试时长（分钟）',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    total_score INT DEFAULT 0 COMMENT '总分',
    status INT DEFAULT 1 COMMENT '状态 1=已发布 2=已结束',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

CREATE TABLE exam_question (
    id BIGINT NOT NULL COMMENT '主键',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    score INT DEFAULT 0 COMMENT '分值',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目关联表';

CREATE TABLE exam_record (
    id BIGINT NOT NULL COMMENT '主键',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_score INT DEFAULT 0 COMMENT '总得分',
    submit_time DATETIME COMMENT '交卷时间',
    status INT DEFAULT 1 COMMENT '状态 1=已开始 2=已完成 3=部分判题中 4=已提交',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_user_id (user_id),
    INDEX idx_exam_user (exam_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

CREATE TABLE exam_answer (
    id BIGINT NOT NULL COMMENT '主键',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    answer TEXT COMMENT '用户答案',
    language INT COMMENT '编程语言 1=Java 2=Python 3=C++ 4=C 5=JavaScript 6=Go',
    status INT DEFAULT 0 COMMENT '判题状态 0=PENDING 1=RUNNING 2=ACCEPTED 3=WRONG_ANSWER 4=COMPILE_ERROR 5=RUNTIME_ERROR 6=TIME_LIMIT_EXCEEDED 7=MEMORY_LIMIT_EXCEEDED',
    score INT DEFAULT 0 COMMENT '该题得分',
    submission_id BIGINT COMMENT '关联judge模块submission.id',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    PRIMARY KEY (id),
    INDEX idx_submission_id (submission_id),
    INDEX idx_exam_user (exam_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户答题记录表';
