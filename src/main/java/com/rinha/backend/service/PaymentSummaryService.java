package com.rinha.backend.service;

import com.rinha.backend.dto.PaymentsSummaryResponse;
import com.rinha.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentSummaryService {

    private final PaymentRepository paymentRepository;
    
    public PaymentSummaryService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentsSummaryResponse getPaymentsSummary() {
        Long processedPayments = paymentRepository.countProcessedPayments();
        BigDecimal processedAmount = paymentRepository.sumProcessedPaymentsAmount();
        BigDecimal processedFees = paymentRepository.sumProcessedPaymentsFees();
        BigDecimal processedNetAmount = paymentRepository.sumProcessedPaymentsNetAmount();
        Long defaultProcessorCount = paymentRepository.countProcessedPaymentsByDefaultProcessor();
        Long fallbackProcessorCount = paymentRepository.countProcessedPaymentsByFallbackProcessor();
        
        // Tratar valores nulos
        if (processedAmount == null) processedAmount = BigDecimal.ZERO;
        if (processedFees == null) processedFees = BigDecimal.ZERO;
        if (processedNetAmount == null) processedNetAmount = BigDecimal.ZERO;
        
        PaymentsSummaryResponse.ProcessorSummary processorSummary = new PaymentsSummaryResponse.ProcessorSummary(
                defaultProcessorCount,
                fallbackProcessorCount
        );
        
        return new PaymentsSummaryResponse(
                processedPayments,
                processedAmount,
                processedFees,
                processedNetAmount,
                processorSummary
        );
    }
}