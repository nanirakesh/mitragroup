package com.mitra.service;

import com.mitra.model.Payment;
import com.mitra.model.ServiceRequest;
import com.mitra.repository.PaymentRepository;
import com.mitra.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    /**
     * Create payment for service request
     */
    public Payment createPayment(Long serviceRequestId, Double amount, Payment.PaymentMethod paymentMethod) {
        Optional<ServiceRequest> requestOpt = serviceRequestRepository.findById(serviceRequestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Service request not found");
        }
        
        ServiceRequest serviceRequest = requestOpt.get();
        
        // Check if payment already exists
        Optional<Payment> existingPayment = paymentRepository.findByServiceRequest(serviceRequest);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }
        
        Payment payment = new Payment(serviceRequest, amount, paymentMethod);
        payment.setTransactionId(generateTransactionId());
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Process payment (Mock implementation - always succeeds)
     */
    public Payment processPayment(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found");
        }
        
        Payment payment = paymentOpt.get();
        
        // Mock payment processing - simulate different outcomes
        boolean paymentSuccess = simulatePaymentGateway(payment);
        
        if (paymentSuccess) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            payment.setPaymentGatewayResponse("Payment successful - Mock Gateway");
            
            // Update service request payment status
            ServiceRequest serviceRequest = payment.getServiceRequest();
            serviceRequest.setPaymentStatus(ServiceRequest.PaymentStatus.PAID);
            serviceRequestRepository.save(serviceRequest);
            
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setPaymentGatewayResponse("Payment failed - Mock Gateway");
        }
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Mock payment gateway simulation
     */
    private boolean simulatePaymentGateway(Payment payment) {
        // Simulate payment processing delay
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Mock different payment scenarios based on amount
        if (payment.getAmount() <= 0) {
            return false; // Invalid amount
        }
        
        if (payment.getAmount() > 50000) {
            return Math.random() > 0.3; // 70% success for high amounts
        }
        
        return Math.random() > 0.1; // 90% success for normal amounts
    }
    
    /**
     * Get payment by service request
     */
    public Optional<Payment> getPaymentByServiceRequest(ServiceRequest serviceRequest) {
        return paymentRepository.findByServiceRequest(serviceRequest);
    }
    
    /**
     * Get payment by transaction ID
     */
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Calculate service cost based on service type and priority
     */
    public Double calculateServiceCost(ServiceRequest serviceRequest) {
        Double baseCost = getBaseCostForService(serviceRequest.getServiceType());
        Double priorityMultiplier = getPriorityMultiplier(serviceRequest.getPriority());
        
        return baseCost * priorityMultiplier;
    }
    
    private Double getBaseCostForService(ServiceRequest.ServiceType serviceType) {
        switch (serviceType) {
            case PLUMBING: return 500.0;
            case ELECTRICAL: return 600.0;
            case CLEANING: return 300.0;
            case CARPENTRY: return 800.0;
            case PAINTING: return 1000.0;
            case APPLIANCE_REPAIR: return 400.0;
            default: return 500.0;
        }
    }
    
    private Double getPriorityMultiplier(ServiceRequest.Priority priority) {
        switch (priority) {
            case LOW: return 1.0;
            case MEDIUM: return 1.2;
            case HIGH: return 1.5;
            case URGENT: return 2.0;
            default: return 1.0;
        }
    }
}