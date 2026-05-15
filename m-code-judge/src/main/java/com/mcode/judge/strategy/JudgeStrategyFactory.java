package com.mcode.judge.strategy;

import com.mcode.common.enums.QuestionTypeEnum;
import com.mcode.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JudgeStrategyFactory {

    private final List<JudgeStrategy> strategies;
    private final Map<QuestionTypeEnum, JudgeStrategy> strategyMap = new EnumMap<>(QuestionTypeEnum.class);

    @PostConstruct
    public void init() {
        for (JudgeStrategy strategy : strategies) {
            strategyMap.put(strategy.supportedType(), strategy);
        }
    }

    public JudgeStrategy getStrategy(QuestionTypeEnum type) {
        JudgeStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BusinessException("不支持的题目类型: " + type.getDesc());
        }
        return strategy;
    }
}
