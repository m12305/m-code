package com.mcode.ai.config;



import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public ChatClient chatClient1(OpenAiChatModel chatModel){
        return ChatClient.create(chatModel);
    }

}
