package com.mcode.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcode.question.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
