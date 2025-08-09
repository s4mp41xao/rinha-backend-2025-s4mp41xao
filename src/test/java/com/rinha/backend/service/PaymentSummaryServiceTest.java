package com.rinha.backend.service;

import com.rinha.backend.dto.PaymentsSummaryResponse;
import com.rinha.backend.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PaymentSummaryServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentSummaryService paymentSummaryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPaymentsSummary() {
        // Arrange
        when(paymentRepository.countProcessedPayments()).thenReturn(10L);
        when(paymentRepository.sumProcessedPaymentsAmount()).thenReturn(new BigDecimal("1000.00"));
        when(paymentRepository.sumProcessedPaymentsFees()).thenReturn(new BigDecimal("50.00"));
        when(paymentRepository.sumProcessedPaymentsNetAmount()).thenReturn(new BigDecimal("950.00"));
        when(paymentRepository.countProcessedPaymentsByDefaultProcessor()).thenReturn(8L);
        when(paymentRepository.countProcessedPaymentsByFallbackProcessor()).thenReturn(2L);

        // Act
        PaymentsSummaryResponse result = paymentSummaryService.getPaymentsSummary();

        // Assert
        assertEquals(10L, result.getProcessedPayments());
        assertEquals(new BigDecimal("1000.00"), result.getProcessedAmount());
        assertEquals(new BigDecimal("50.00"), result.getProcessedFees());
        assertEquals(new BigDecimal("950.00"), result.getProcessedNetAmount());
        assertEquals(8L, result.getProcessors().getDefaultProcessor());
        assertEquals(2L, result.getProcessors().getFallbackProcessor());
    }

    @Test
    public void testGetPaymentsSummaryWithNullValues() {
        // Arrange
        when(paymentRepository.countProcessedPayments()).thenReturn(0L);
        when(paymentRepository.sumProcessedPaymentsAmount()).thenReturn(null);
        when(paymentRepository.sumProcessedPaymentsFees()).thenReturn(null);
        when(paymentRepository.sumProcessedPaymentsNetAmount()).thenReturn(null);
        when(paymentRepository.countProcessedPaymentsByDefaultProcessor()).thenReturn(0L);
        when(paymentRepository.countProcessedPaymentsByFallbackProcessor()).thenReturn(0L);

        // Act
        PaymentsSummaryResponse result = paymentSummaryService.getPaymentsSummary();

        // Assert
        assertEquals(0L, result.getProcessedPayments());
        assertEquals(BigDecimal.ZERO, result.getProcessedAmount());
        assertEquals(BigDecimal.ZERO, result.getProcessedFees());
        assertEquals(BigDecimal.ZERO, result.getProcessedNetAmount());
        assertEquals(0L, result.getProcessors().getDefaultProcessor());
        assertEquals(0L, result.getProcessors().getFallbackProcessor());
    }
}