package com.mcode.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mcode.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learning_path")
public class LearningPath extends BaseEntity {

    private String title;
    private String description;
    private String coverImage;
    private String articleIds;
    private Integer sort;
    private Integer status;
}
