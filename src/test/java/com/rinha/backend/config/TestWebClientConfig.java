package com.rinha.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestWebClientConfig {

    @Bean
    @Primary
    public WebClient defaultProcessorWebClient() {
        return mock(WebClient.class);
    }

    @Bean
    @Primary
    public WebClient fallbackProcessorWebClient() {
        return mock(WebClient.class);
    }
}