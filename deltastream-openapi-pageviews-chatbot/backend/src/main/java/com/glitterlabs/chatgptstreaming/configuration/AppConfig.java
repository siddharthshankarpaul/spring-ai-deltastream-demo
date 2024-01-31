package com.glitterlabs.chatgptstreaming.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@Getter
public class AppConfig {


    @Value("classpath:/rag-prompt-template.st")
    private Resource prompt;
    @Value("classpath:/user-level-rules.txt")
    private Resource userLevelRules;
    @Value("classpath:/user-membership-rules.txt")
    private Resource userMembershipRules;

    @Value("${spring.ai.vectorstore.milvus.collectionName}")
    @Getter
    private String collectionName;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
