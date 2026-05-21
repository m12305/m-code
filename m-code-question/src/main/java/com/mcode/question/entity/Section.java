package com.mcode.question.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question_section")
public class Section extends BaseEntity {

    private String name;
    private String description;
    private String icon;
    private Integer sort;
    private Integer status;
}
