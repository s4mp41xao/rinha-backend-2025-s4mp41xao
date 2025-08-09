package com.rinha.backend.service;

import com.rinha.backend.dto.PaymentRequest;
import com.rinha.backend.dto.PaymentResponse;
import com.rinha.backend.model.Payment;
import com.rinha.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PaymentProcessorService {
    
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorService.class);

    private final WebClient defaultProcessorWebClient;
    private final WebClient fallbackProcessorWebClient;
    private final PaymentRepository paymentRepository;
    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;
    
    public PaymentProcessorService(WebClient defaultProcessorWebClient, 
                                  WebClient fallbackProcessorWebClient,
                                  PaymentRepository paymentRepository,
                                  ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.defaultProcessorWebClient = defaultProcessorWebClient;
        this.fallbackProcessorWebClient = fallbackProcessorWebClient;
        this.paymentRepository = paymentRepository;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Value("${payment.processor.default.fee}")
    private BigDecimal defaultProcessorFee;

    @Value("${payment.processor.fallback.fee}")
    private BigDecimal fallbackProcessorFee;

    @Value("${payment.processor.timeout}")
    private int timeout;

    @Value("${payment.processor.retry.max-attempts}")
    private int maxRetryAttempts;

    @Value("${payment.processor.retry.delay}")
    private int retryDelay;

    public Mono<PaymentResponse> processPayment(PaymentRequest request) {
        // Verificar se já existe um pagamento com o mesmo correlationId
        if (paymentRepository.findByCorrelationId(request.getCorrelationId()).isPresent()) {
            return Mono.error(new IllegalArgumentException("Payment with this correlationId already exists"));
        }

        // Tentar processar com o processador padrão
        return processWithDefaultProcessor(request)
                .onErrorResume(e -> {
                    log.error("Error processing payment with default processor: {}", e.getMessage());
                    // Em caso de erro, tentar com o processador de fallback
                    return processWithFallbackProcessor(request);
                });
    }

    private Mono<PaymentResponse> processWithDefaultProcessor(PaymentRequest request) {
        return defaultProcessorWebClient.post()
                .uri("/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .retryWhen(Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
                        .filter(e -> !(e instanceof WebClientResponseException.BadRequest)))
                .transform(it -> circuitBreakerFactory.create("defaultProcessor")
                        .run(it, throwable -> Mono.error(new RuntimeException("Default processor unavailable", throwable))))
                .doOnSuccess(response -> savePayment(request, response, "default", "PROCESSED"))
                .doOnError(e -> savePayment(request, null, "default", "FAILED"));
    }

    private Mono<PaymentResponse> processWithFallbackProcessor(PaymentRequest request) {
        return fallbackProcessorWebClient.post()
                .uri("/payments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .retryWhen(Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
                        .filter(e -> !(e instanceof WebClientResponseException.BadRequest)))
                .transform(it -> circuitBreakerFactory.create("fallbackProcessor")
                        .run(it, throwable -> Mono.error(new RuntimeException("Fallback processor unavailable", throwable))))
                .doOnSuccess(response -> savePayment(request, response, "fallback", "PROCESSED"))
                .doOnError(e -> savePayment(request, null, "fallback", "FAILED"));
    }

    private void savePayment(PaymentRequest request, PaymentResponse response, String processor, String status) {
        BigDecimal fee = "default".equals(processor) ? defaultProcessorFee : fallbackProcessorFee;
        BigDecimal amount = request.getAmount();
        BigDecimal feeAmount = amount.multiply(fee);
        BigDecimal netAmount = amount.subtract(feeAmount);

        Payment payment = Payment.builder()
                .correlationId(request.getCorrelationId())
                .amount(amount)
                .fee(feeAmount)
                .netAmount(netAmount)
                .processor(processor)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
    }
}