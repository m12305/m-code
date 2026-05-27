package com.mcode.ai.service.impl;

import com.mcode.ai.dto.ChatRequest;
import com.mcode.ai.dto.ExplainRequest;
import com.mcode.ai.dto.HintRequest;
import com.mcode.ai.dto.ScoreRequest;
import com.mcode.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    //private final OpenAiApi.ChatModel chatModel;

    private  final ChatClient chatClient1;

    private static final String SYSTEM_CHAT = "你是一个专业的AI知识小助手，帮助用户解决问题。请用中文回答。";
    private static final String SYSTEM_HINT = "你是一个编程导师。你的任务是给学生提供解题提示，而不是直接给出答案。请逐步引导他们思考问题。请用中文回答。";
    private static final String SYSTEM_EXPLAIN = "你是一个代码讲解员。请清晰、详细地解释用户提供的代码逻辑和关键实现。请用中文回答。";
    private static final String SYSTEM_SCORE = """
            你是一个严格但公正的阅卷老师。请根据题目描述和参考答案来评判学生的作答。
            请以JSON格式返回评分结果，格式如下：
            {
              "score": <整数，得分0-10>,
              "maxScore": 10,
              "feedback": "<总体评价>",
              "correctPoints": ["<答对的要点1>", "<答对的要点2>"],
              "missedPoints": ["<遗漏的要点1>", "<遗漏的要点2>"],
              "suggestions": "<改进建议>"
            }
            请只返回JSON，不要包含其他文字。""";

    @Override
    public String chat(ChatRequest request) {
        List<Message> messages = buildMessages(SYSTEM_CHAT, request);
        return chatClient1.prompt(new Prompt(messages))
                .call()
                .content();
    }

    @Override
    public Flux<String> chatStream(ChatRequest request) {
        List<Message> messages = buildMessages(SYSTEM_CHAT, request);
        return chatClient1.prompt(new Prompt(messages))
                .stream()
                .content();
    }

    @Override
    public String hint(HintRequest request) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_HINT));

        StringBuilder sb = new StringBuilder();
        sb.append("题目描述：\n").append(request.getProblemDescription());
        if (request.getDifficulty() != null) {
            sb.append("\n\n难度：").append(request.getDifficulty());
        }
        if (request.getQuestionType() != null) {
            sb.append("\n\n题型：").append(request.getQuestionType());
        }
        sb.append("\n\n请给学生一些解题提示，不要直接给出答案。");
        messages.add(new UserMessage(sb.toString()));
        return chatClient1.prompt(new Prompt(messages))
                .call()
                .content();
    }

    @Override
    public String explain(ExplainRequest request) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_EXPLAIN));

        StringBuilder sb = new StringBuilder();
        if (request.getLanguage() != null) {
            sb.append("语言：").append(request.getLanguage()).append("\n\n");
        }
        sb.append("代码：\n```\n").append(request.getCode()).append("\n```");
        sb.append("\n\n请解释以上代码。");
        messages.add(new UserMessage(sb.toString()));

        return chatClient1.prompt(new Prompt(messages))
                .call()
                .content();
    }

    @Override
    public String score(ScoreRequest request) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_SCORE));

        StringBuilder sb = new StringBuilder();
        sb.append("题目描述：\n").append(request.getQuestionDescription());
        sb.append("\n\n参考答案（评分要点）：\n").append(request.getReferenceAnswer());
        sb.append("\n\n学生答案：\n").append(request.getUserAnswer());
        sb.append("\n\n请根据以上信息评分学生的作答，只返回JSON格式结果。");
        messages.add(new UserMessage(sb.toString()));

        return chatClient1.prompt(new Prompt(messages))
                .call()
                .content();
    }

    private List<Message> buildMessages(String systemPrompt, ChatRequest request) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        if (request.getMessages() != null) {
            for (ChatRequest.Message msg : request.getMessages()) {
                if ("system".equals(msg.getRole())) {
                    messages.add(new SystemMessage(msg.getContent()));
                } else {
                    messages.add(new UserMessage(msg.getContent()));
                }
            }
        }
        return messages;
    }
}
