package com.rinha.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsSummaryResponse {

    private Long processedPayments;
    private BigDecimal processedAmount;
    private BigDecimal processedFees;
    private BigDecimal processedNetAmount;
    private ProcessorSummary processors;
    
    public Long getProcessedPayments() {
        return processedPayments;
    }
    
    public void setProcessedPayments(Long processedPayments) {
        this.processedPayments = processedPayments;
    }
    
    public BigDecimal getProcessedAmount() {
        return processedAmount;
    }
    
    public void setProcessedAmount(BigDecimal processedAmount) {
        this.processedAmount = processedAmount;
    }
    
    public BigDecimal getProcessedFees() {
        return processedFees;
    }
    
    public void setProcessedFees(BigDecimal processedFees) {
        this.processedFees = processedFees;
    }
    
    public BigDecimal getProcessedNetAmount() {
        return processedNetAmount;
    }
    
    public void setProcessedNetAmount(BigDecimal processedNetAmount) {
        this.processedNetAmount = processedNetAmount;
    }
    
    public ProcessorSummary getProcessors() {
        return processors;
    }
    
    public void setProcessors(ProcessorSummary processors) {
        this.processors = processors;
    }
    
    public static class ProcessorSummary {
        
        public ProcessorSummary() {
        }
        
        public ProcessorSummary(Long defaultProcessor, Long fallbackProcessor) {
            this.defaultProcessor = defaultProcessor;
            this.fallbackProcessor = fallbackProcessor;
        }
        private Long defaultProcessor;
        private Long fallbackProcessor;
        
        public Long getDefaultProcessor() {
            return defaultProcessor;
        }
        
        public void setDefaultProcessor(Long defaultProcessor) {
            this.defaultProcessor = defaultProcessor;
        }
        
        public Long getFallbackProcessor() {
            return fallbackProcessor;
        }
        
        public void setFallbackProcessor(Long fallbackProcessor) {
            this.fallbackProcessor = fallbackProcessor;
        }
        
        public static ProcessorSummaryBuilder builder() {
            return new ProcessorSummaryBuilder();
        }
        
        public static class ProcessorSummaryBuilder {
            private Long defaultProcessor;
            private Long fallbackProcessor;
            
            ProcessorSummaryBuilder() {
            }
            
            public ProcessorSummaryBuilder defaultProcessor(Long defaultProcessor) {
                this.defaultProcessor = defaultProcessor;
                return this;
            }
            
            public ProcessorSummaryBuilder fallbackProcessor(Long fallbackProcessor) {
                this.fallbackProcessor = fallbackProcessor;
                return this;
            }
            
            public ProcessorSummary build() {
                return new ProcessorSummary(defaultProcessor, fallbackProcessor);
            }
        }
    }
    
    public static PaymentsSummaryResponseBuilder builder() {
        return new PaymentsSummaryResponseBuilder();
    }
    
    public static class PaymentsSummaryResponseBuilder {
        private Long processedPayments;
        private BigDecimal processedAmount;
        private BigDecimal processedFees;
        private BigDecimal processedNetAmount;
        private ProcessorSummary processors;
        
        PaymentsSummaryResponseBuilder() {
        }
        
        public PaymentsSummaryResponseBuilder processedPayments(Long processedPayments) {
            this.processedPayments = processedPayments;
            return this;
        }
        
        public PaymentsSummaryResponseBuilder processedAmount(BigDecimal processedAmount) {
            this.processedAmount = processedAmount;
            return this;
        }
        
        public PaymentsSummaryResponseBuilder processedFees(BigDecimal processedFees) {
            this.processedFees = processedFees;
            return this;
        }
        
        public PaymentsSummaryResponseBuilder processedNetAmount(BigDecimal processedNetAmount) {
            this.processedNetAmount = processedNetAmount;
            return this;
        }
        
        public PaymentsSummaryResponseBuilder processors(ProcessorSummary processors) {
            this.processors = processors;
            return this;
        }
        
        public PaymentsSummaryResponse build() {
            return new PaymentsSummaryResponse(processedPayments, processedAmount, processedFees, processedNetAmount, processors);
        }
    }
}