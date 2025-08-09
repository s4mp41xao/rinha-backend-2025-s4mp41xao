package com.rinha.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${payment.processor.default.url}")
    private String defaultProcessorUrl;

    @Value("${payment.processor.fallback.url}")
    private String fallbackProcessorUrl;

    @Bean
    public WebClient defaultProcessorWebClient() {
        return WebClient.builder()
                .baseUrl(defaultProcessorUrl)
                .build();
    }

    @Bean
    public WebClient fallbackProcessorWebClient() {
        return WebClient.builder()
                .baseUrl(fallbackProcessorUrl)
                .build();
    }
}