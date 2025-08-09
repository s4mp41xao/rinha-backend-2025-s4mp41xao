package com.rinha.backend.repository;

import com.rinha.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCorrelationId(String correlationId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PROCESSED'")
    Long countProcessedPayments();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PROCESSED'")
    BigDecimal sumProcessedPaymentsAmount();
    
    @Query("SELECT SUM(p.fee) FROM Payment p WHERE p.status = 'PROCESSED'")
    BigDecimal sumProcessedPaymentsFees();
    
    @Query("SELECT SUM(p.netAmount) FROM Payment p WHERE p.status = 'PROCESSED'")
    BigDecimal sumProcessedPaymentsNetAmount();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.processor = 'default' AND p.status = 'PROCESSED'")
    Long countProcessedPaymentsByDefaultProcessor();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.processor = 'fallback' AND p.status = 'PROCESSED'")
    Long countProcessedPaymentsByFallbackProcessor();
}