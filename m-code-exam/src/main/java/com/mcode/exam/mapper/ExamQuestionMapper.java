package com.mcode.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcode.exam.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
}
