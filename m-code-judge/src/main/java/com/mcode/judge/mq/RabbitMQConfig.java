package com.mcode.judge.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "judge.exchange";
    public static final String QUEUE = "judge.queue";
    public static final String ROUTING_KEY = "judge.submit";

    @Bean
    public TopicExchange judgeExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue judgeQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding judgeBinding() {
        return BindingBuilder.bind(judgeQueue()).to(judgeExchange()).with(ROUTING_KEY);
    }

    // 全局使用 JSON 消息转换器
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
