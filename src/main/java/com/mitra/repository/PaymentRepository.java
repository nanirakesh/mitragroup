package com.mitra.repository;

import com.mitra.model.Payment;
import com.mitra.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByServiceRequest(ServiceRequest serviceRequest);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    Optional<Payment> findByTransactionId(String transactionId);
}