package com.mcode.ai.service;

import com.mcode.ai.dto.ChatRequest;
import com.mcode.ai.dto.ExplainRequest;
import com.mcode.ai.dto.HintRequest;
import com.mcode.ai.dto.ScoreRequest;
import reactor.core.publisher.Flux;

public interface AiService {

    String chat(ChatRequest request);

    Flux<String> chatStream(ChatRequest request);

    String hint(HintRequest request);

    String explain(ExplainRequest request);

    String score(ScoreRequest request);
}
