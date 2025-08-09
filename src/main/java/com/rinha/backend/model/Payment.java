package com.rinha.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    
    public Payment(Long id, String correlationId, BigDecimal amount, BigDecimal fee, BigDecimal netAmount, LocalDateTime createdAt, String processor, String status) {
        this.id = id;
        this.correlationId = correlationId;
        this.amount = amount;
        this.fee = fee;
        this.netAmount = netAmount;
        this.createdAt = createdAt;
        this.processor = processor;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String correlationId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal netAmount;
    private LocalDateTime createdAt;
    private String processor; // "default" ou "fallback"
    private String status; // "PROCESSED", "FAILED", "PENDING"
    
    public Payment() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getProcessor() {
        return processor;
    }
    
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Explicit builder method
    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }
    
    // Builder class
    public static class PaymentBuilder {
        private Long id;
        private String correlationId;
        private BigDecimal amount;
        private BigDecimal fee;
        private BigDecimal netAmount;
        private LocalDateTime createdAt;
        private String processor;
        private String status;
        
        PaymentBuilder() {
        }
        
        public PaymentBuilder id(Long id) {
            this.id = id;
            return this;
        }
        
        public PaymentBuilder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public PaymentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public PaymentBuilder fee(BigDecimal fee) {
            this.fee = fee;
            return this;
        }
        
        public PaymentBuilder netAmount(BigDecimal netAmount) {
            this.netAmount = netAmount;
            return this;
        }
        
        public PaymentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public PaymentBuilder processor(String processor) {
            this.processor = processor;
            return this;
        }
        
        public PaymentBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public Payment build() {
            return new Payment(id, correlationId, amount, fee, netAmount, createdAt, processor, status);
        }
    }
}