package com.mitra.repository;

import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUser(User user);
    List<ServiceRequest> findByStatus(ServiceRequest.Status status);
    List<ServiceRequest> findByServiceType(ServiceRequest.ServiceType serviceType);
    List<ServiceRequest> findByAssignedProviderIsNull();
    List<ServiceRequest> findByAssignedProvider_Id(Long providerId);
}