package com.mcode.judge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mcode.judge.entity.Submission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {
}
