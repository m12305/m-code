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

    //Spring 会自动把所有实现了 JudgeStrategy 接口的类全部收集到这个 list 里
    private final List<JudgeStrategy> strategies;
    private final Map<QuestionTypeEnum, JudgeStrategy> strategyMap = new EnumMap<>(QuestionTypeEnum.class);

    //遍历所有策略,调用每个策略的 supportedType(),题目  类型 → 策略 放进 strategyMap
    //@PostConstruct关键字 在 Spring 把对象创建好、所有依赖注入完成之后，自动执行这个方法
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
