-- 提交记录表
CREATE TABLE submission (
    id            BIGINT        NOT NULL COMMENT '主键（主键ID）',
    user_id       BIGINT        NOT NULL COMMENT '用户ID',
    question_id   BIGINT        NOT NULL COMMENT '题目ID',
    answer        TEXT          NULL     COMMENT '作答内容（编程题存代码，选择题存选项，判断题存答案，简答题存文本）',
    language      TINYINT       NULL     COMMENT '编程语言（1-Java 2-Python 3-C 等，仅编程题使用）',
    status        TINYINT       NOT NULL DEFAULT 0 COMMENT '判题状态 0-等待判题 1-判题中 2-通过 3-答案错误 4-编译错误 5-运行错误 6-运行超时 7-内存溢出',
    time_used     INT           NULL     COMMENT '运行耗时(ms)',
    memory_used   INT           NULL     COMMENT '运行内存(KB)',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提交记录表';

-- 判题结果明细表
CREATE TABLE judge_result (
    id              BIGINT      NOT NULL COMMENT '主键（主键ID）',
    submission_id   BIGINT      NOT NULL COMMENT '提交记录ID',
    test_case_name  VARCHAR(64) NULL     COMMENT '测试用例名称',
    status          TINYINT     NOT NULL DEFAULT 0 COMMENT '判题状态 0-等待判题 1-判题中 2-通过 3-答案错误 4-编译错误 5-运行错误 6-运行超时 7-内存溢出',
    actual_output   TEXT        NULL     COMMENT '实际输出',
    error_message   TEXT        NULL     COMMENT '错误信息',
    time_used       INT         NULL     COMMENT '运行耗时(ms)',
    memory_used     INT         NULL     COMMENT '运行内存(KB)',
    create_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (id),
    INDEX idx_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='判题结果明细表';
