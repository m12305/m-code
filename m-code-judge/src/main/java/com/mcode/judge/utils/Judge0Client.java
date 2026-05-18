package com.mcode.judge.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcode.common.enums.JudgeStatusEnum;
import com.mcode.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Judge0Client {

    private static final String BASE_URL = "http://127.0.0.1:2358";

    private static final Map<Integer, Integer> LANGUAGE_MAP = Map.of(
            1, 62,  // JAVA
            2, 71,  // Python 3
            3, 54,  // C++ GCC
            4, 50,  // C GCC
            5, 63,  // JavaScript Node.js
            6, 60   // Go
    );

    private final RestTemplate restTemplate;

    public Judge0Result judge(String sourceCode, Integer languageCode, String stdin, String expectedOutput) {
        Integer langId = LANGUAGE_MAP.get(languageCode);
        if (langId == null) {
            throw new BusinessException("不支持的编程语言");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("source_code", sourceCode);
        body.put("language_id", langId);
        body.put("stdin", stdin != null ? stdin : "");
        body.put("expected_output", expectedOutput != null ? expectedOutput : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(body,headers);

        String url = BASE_URL + "/submissions?base64_encoded=false&wait=true";
        //log.info("调用Judge0判题: langId={}, stdin={}, body={}", langId, stdin, body);
        log.info("调用Judge0判题: langId={}, stdin={}", langId, stdin);

        JsonNode response = restTemplate.postForObject(url, request, JsonNode.class);


        if (response==null){throw new BusinessException("Judge0返回异常");}
        return parseResponse(response);

    }

    private Judge0Result parseResponse(JsonNode node) {
        String token= node.get("token").asText();

        String url = BASE_URL + "/submissions/" + token +"?base64_encoded=false";
        JsonNode status = restTemplate.getForObject(url, JsonNode.class);
        if (status == null) {
            throw new BusinessException("Judge0返回异常：无status字段");
        }

        Judge0Result result = new Judge0Result();
        result.setStatusId(status.get("status").get("id").asInt());
        result.setStatusDesc(status.get("status").get("description").asText());
        result.setStdout(status.has("stdout") && !status.get("stdout").isNull() ? status.get("stdout").asText().trim() : null);
        result.setStderr(status.has("stderr") && !status.get("stderr").isNull() ? status.get("stderr").asText() : null);
        result.setCompileOutput(status.has("compile_output") && !status.get("compile_output").isNull() ? status.get("compile_output").asText() : null);
        result.setMessage(status.has("message") && !status.get("message").isNull() ? status.get("message").asText() : null);
        result.setTime(status.has("time") && !status.get("time").isNull() ? status.get("time").asText() : "0");
        result.setMemory(status.has("memory") && !status.get("memory").isNull() ? status.get("memory").asInt() : 0);
        log.info("result:{}",result);
        return result;
    }

    public JudgeStatusEnum mapStatus(int judge0StatusId) {
        return switch (judge0StatusId) {
            case 3 -> JudgeStatusEnum.ACCEPTED;
            case 4 -> JudgeStatusEnum.WRONG_ANSWER;
            case 5 -> JudgeStatusEnum.TIME_LIMIT_EXCEEDED;
            case 6, 14 -> JudgeStatusEnum.COMPILE_ERROR;
            default -> JudgeStatusEnum.RUNTIME_ERROR;
        };
    }
}
