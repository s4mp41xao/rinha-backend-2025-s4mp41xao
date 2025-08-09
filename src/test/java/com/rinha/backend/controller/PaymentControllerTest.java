package com.rinha.backend.controller;

import com.rinha.backend.dto.PaymentRequest;
import com.rinha.backend.dto.PaymentResponse;
import com.rinha.backend.dto.PaymentsSummaryResponse;
import com.rinha.backend.service.PaymentProcessorService;
import com.rinha.backend.service.PaymentSummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = PaymentController.class)
@org.springframework.test.context.ActiveProfiles("test")
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PaymentProcessorService paymentProcessorService;

    @MockBean
    private PaymentSummaryService paymentSummaryService;

    @Test
    public void testProcessPayment() {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCorrelationId("test-correlation-id");

        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setCorrelationId("test-correlation-id");
        mockResponse.setAmount(new BigDecimal("100.00"));
        mockResponse.setFee(new BigDecimal("5.00"));
        mockResponse.setNetAmount(new BigDecimal("95.00"));

        // Mock the PaymentProcessorService instead of WebClient
        when(paymentProcessorService.processPayment(any(PaymentRequest.class)))
            .thenReturn(Mono.just(mockResponse));

        // Act & Assert
        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.correlationId").isEqualTo("test-correlation-id")
                .jsonPath("$.amount").isEqualTo(100.00)
                .jsonPath("$.fee").isEqualTo(5.00)
                .jsonPath("$.netAmount").isEqualTo(95.00);
    }

    @Test
    public void testGetPaymentsSummary() {
        // Arrange
        PaymentsSummaryResponse.ProcessorSummary processorSummary = new PaymentsSummaryResponse.ProcessorSummary();
        processorSummary.setDefaultProcessor(8L);
        processorSummary.setFallbackProcessor(2L);

        PaymentsSummaryResponse summaryResponse = new PaymentsSummaryResponse();
        summaryResponse.setProcessedPayments(10L);
        summaryResponse.setProcessedAmount(new BigDecimal("1000.00"));
        summaryResponse.setProcessedFees(new BigDecimal("50.00"));
        summaryResponse.setProcessedNetAmount(new BigDecimal("950.00"));
        summaryResponse.setProcessors(processorSummary);

        // Mock the PaymentSummaryService directly
        when(paymentSummaryService.getPaymentsSummary()).thenReturn(summaryResponse);

        // Act & Assert
        webTestClient.get()
                .uri("/payments-summary")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.processedPayments").isEqualTo(10)
                .jsonPath("$.processedAmount").isEqualTo(1000.00)
                .jsonPath("$.processedFees").isEqualTo(50.00)
                .jsonPath("$.processedNetAmount").isEqualTo(950.00)
                .jsonPath("$.processors.defaultProcessor").isEqualTo(8)
                .jsonPath("$.processors.fallbackProcessor").isEqualTo(2);
    }
}