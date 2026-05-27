package com.mcode.ai.controller;

import com.mcode.ai.dto.ChatRequest;
import com.mcode.ai.dto.ExplainRequest;
import com.mcode.ai.dto.HintRequest;
import com.mcode.ai.dto.ScoreRequest;
import com.mcode.ai.service.AiService;
import com.mcode.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody ChatRequest request) {
        return Result.ok(aiService.chat(request));
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return aiService.chatStream(request);
    }

    @PostMapping("/hint")
    public Result<String> hint(@RequestBody HintRequest request) {
        return Result.ok(aiService.hint(request));
    }

    @PostMapping("/explain")
    public Result<String> explain(@RequestBody ExplainRequest request) {
        return Result.ok(aiService.explain(request));
    }

    @PostMapping("/score")
    public Result<String> score(@RequestBody ScoreRequest request) {
        return Result.ok(aiService.score(request));
    }
}
