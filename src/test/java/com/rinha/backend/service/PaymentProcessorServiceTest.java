package com.rinha.backend.service;

import com.rinha.backend.dto.PaymentRequest;
import com.rinha.backend.dto.PaymentResponse;
import com.rinha.backend.model.Payment;
import com.rinha.backend.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorServiceTest {

    @Mock
    private WebClient defaultProcessorWebClient;

    @Mock
    private WebClient fallbackProcessorWebClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReactiveCircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private ReactiveCircuitBreaker circuitBreaker;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    private PaymentProcessorService paymentProcessorService;

    @BeforeEach
    void setUp() {
        paymentProcessorService = new PaymentProcessorService(
                defaultProcessorWebClient,
                fallbackProcessorWebClient,
                paymentRepository,
                circuitBreakerFactory
        );

        ReflectionTestUtils.setField(paymentProcessorService, "defaultProcessorFee", new BigDecimal("0.05"));
        ReflectionTestUtils.setField(paymentProcessorService, "fallbackProcessorFee", new BigDecimal("0.08"));
        ReflectionTestUtils.setField(paymentProcessorService, "timeout", 5000);
        ReflectionTestUtils.setField(paymentProcessorService, "maxRetryAttempts", 3);
        ReflectionTestUtils.setField(paymentProcessorService, "retryDelay", 1000);

        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(Mono.class), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testProcessPayment() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCorrelationId("test-correlation-id");

        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setCorrelationId("test-correlation-id");
        expectedResponse.setAmount(new BigDecimal("100.00"));
        expectedResponse.setFee(new BigDecimal("5.00"));
        expectedResponse.setNetAmount(new BigDecimal("95.00"));

        Payment savedPayment = Payment.builder()
                .id(1L)
                .correlationId("test-correlation-id")
                .amount(new BigDecimal("100.00"))
                .fee(new BigDecimal("5.00"))
                .netAmount(new BigDecimal("95.00"))
                .status("PROCESSED")
                .processor("default")
                .build();

        // Mock repository behavior
        when(paymentRepository.findByCorrelationId("test-correlation-id")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Mock WebClient behavior
        when(defaultProcessorWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("/payments"))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(PaymentRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(PaymentResponse.class))).thenReturn(Mono.just(expectedResponse));

        // Act
        Mono<PaymentResponse> result = paymentProcessorService.processPayment(request);

        // Assert
        PaymentResponse response = result.block();
        assertEquals("test-correlation-id", response.getCorrelationId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(new BigDecimal("5.00"), response.getFee());
        assertEquals(new BigDecimal("95.00"), response.getNetAmount());
    }
}