package com.mcode.judge.feign;

import com.mcode.common.dto.QuestionVO;
import com.mcode.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "m-code-question")
public interface QuestionFeignClient {

    @GetMapping("/api/question/detail/{id}")
    Result<QuestionVO> getQuestionDetail(@PathVariable("id") Long id);
}
