package com.rinha.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class PaymentResponse {

    private String correlationId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal netAmount;
    
    public PaymentResponse() {
    }
    
    public PaymentResponse(String correlationId, BigDecimal amount, BigDecimal fee, BigDecimal netAmount) {
        this.correlationId = correlationId;
        this.amount = amount;
        this.fee = fee;
        this.netAmount = netAmount;
    }
    
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
    
    public BigDecimal getFee() {
        return fee;
    }
    
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public static PaymentResponseBuilder builder() {
        return new PaymentResponseBuilder();
    }
    
    public static class PaymentResponseBuilder {
        private String correlationId;
        private BigDecimal amount;
        private BigDecimal fee;
        private BigDecimal netAmount;
        
        public PaymentResponseBuilder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public PaymentResponseBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public PaymentResponseBuilder fee(BigDecimal fee) {
            this.fee = fee;
            return this;
        }
        
        public PaymentResponseBuilder netAmount(BigDecimal netAmount) {
            this.netAmount = netAmount;
            return this;
        }
        
        public PaymentResponse build() {
            return new PaymentResponse(correlationId, amount, fee, netAmount);
        }
    }
}