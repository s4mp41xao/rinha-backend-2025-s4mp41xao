package com.rinha.backend.controller;

import com.rinha.backend.dto.PaymentRequest;
import com.rinha.backend.dto.PaymentResponse;
import com.rinha.backend.dto.PaymentsSummaryResponse;
import com.rinha.backend.service.PaymentProcessorService;
import com.rinha.backend.service.PaymentSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PaymentController {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentProcessorService paymentProcessorService;
    private final PaymentSummaryService paymentSummaryService;
    
    public PaymentController(PaymentProcessorService paymentProcessorService, 
                            PaymentSummaryService paymentSummaryService) {
        this.paymentProcessorService = paymentProcessorService;
        this.paymentSummaryService = paymentSummaryService;
    }

    @PostMapping("/payments")
    public Mono<ResponseEntity<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Received payment request: {}", request);
        return paymentProcessorService.processPayment(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(e -> {
                    log.error("Error processing payment: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentsSummaryResponse> getPaymentsSummary() {
        log.info("Received request for payments summary");
        try {
            PaymentsSummaryResponse summary = paymentSummaryService.getPaymentsSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting payments summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}