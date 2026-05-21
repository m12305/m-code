package com.mcode.exam.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExamRabbitMQConfig {

    public static final String EXAM_JUDGE_EXCHANGE = "exam.judge.exchange";
    public static final String EXAM_JUDGE_QUEUE = "exam.judge.queue";
    public static final String EXAM_JUDGE_ROUTING_KEY = "exam.judge.request";

    @Bean
    public TopicExchange examJudgeExchange() {
        return new TopicExchange(EXAM_JUDGE_EXCHANGE);
    }

    @Bean
    public Queue examJudgeQueue() {
        return new Queue(EXAM_JUDGE_QUEUE);
    }

    @Bean
    public Binding examJudgeBinding() {
        return BindingBuilder.bind(examJudgeQueue())
                .to(examJudgeExchange())
                .with(EXAM_JUDGE_ROUTING_KEY);
    }

    // 全局使用 JSON 消息转换器
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
