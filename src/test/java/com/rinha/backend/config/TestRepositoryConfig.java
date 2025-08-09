package com.rinha.backend.config;

import com.rinha.backend.repository.PaymentRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestRepositoryConfig {

    @Bean
    @Primary
    public PaymentRepository paymentRepository() {
        return mock(PaymentRepository.class);
    }
}