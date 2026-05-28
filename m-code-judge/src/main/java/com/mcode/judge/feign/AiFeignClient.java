package com.mcode.judge.feign;

import com.mcode.common.result.Result;
import com.mcode.judge.dto.ScoreRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "m-code-ai")
public interface AiFeignClient {

    @PostMapping("/api/ai/score")
    Result<String> score(@RequestBody ScoreRequest request);
}
