package com.rinha.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class TestResilienceConfig {

    @Bean
    @Primary
    public ReactiveCircuitBreakerFactory mockReactiveCircuitBreakerFactory() {
        ReactiveCircuitBreakerFactory mockFactory = mock(ReactiveCircuitBreakerFactory.class);
        org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker mockCircuitBreaker = mock(org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker.class);
        
        when(mockFactory.create(any(String.class))).thenReturn(mockCircuitBreaker);
        when(mockCircuitBreaker.run(any(Mono.class), any())).thenAnswer(invocation -> {
            Mono<?> mono = invocation.getArgument(0);
            return mono;
        });
        
        return mockFactory;
    }
}