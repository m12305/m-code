package com.mcode.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_category")
public class KnowledgeCategory extends BaseEntity {

    private String name;
    private String icon;
    private Long parentId;
    private Integer sort;
}
