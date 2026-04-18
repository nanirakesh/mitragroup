package com.mitra.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "service_request_id")
    private ServiceRequest serviceRequest;
    
    @Column(nullable = false)
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private String transactionId;
    private String paymentGatewayResponse;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime paidAt;
    
    public enum PaymentMethod {
        CASH, UPI, CARD, NET_BANKING, WALLET
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED
    }

    // Constructors
    public Payment() {}
    
    public Payment(ServiceRequest serviceRequest, Double amount, PaymentMethod paymentMethod) {
        this.serviceRequest = serviceRequest;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequest serviceRequest) { this.serviceRequest = serviceRequest; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getPaymentGatewayResponse() { return paymentGatewayResponse; }
    public void setPaymentGatewayResponse(String paymentGatewayResponse) { this.paymentGatewayResponse = paymentGatewayResponse; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}