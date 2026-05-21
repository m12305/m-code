package com.mcode.question.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question_tag")
public class Tag extends BaseEntity {

    private String name;
    private String color;
}
