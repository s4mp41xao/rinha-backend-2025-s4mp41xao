package com.rinha.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class PaymentRequest {
    
    public PaymentRequest() {
    }
    
    public PaymentRequest(String correlationId, BigDecimal amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }

    @NotBlank(message = "correlationId is required")
    private String correlationId;
    
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;
    
    // Explicit getters and setters
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public static PaymentRequestBuilder builder() {
        return new PaymentRequestBuilder();
    }
    
    public static class PaymentRequestBuilder {
        private String correlationId;
        private BigDecimal amount;
        
        public PaymentRequestBuilder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public PaymentRequestBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public PaymentRequest build() {
            return new PaymentRequest(correlationId, amount);
        }
    }
}