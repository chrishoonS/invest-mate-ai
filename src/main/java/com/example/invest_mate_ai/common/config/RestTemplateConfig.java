package com.example.invest_mate_ai.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // RestTemplate Bean Spring 컨테이너에 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}