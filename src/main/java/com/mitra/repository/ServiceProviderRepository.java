package com.mitra.repository;

import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    Optional<ServiceProvider> findByEmail(String email);
    List<ServiceProvider> findByStatus(ServiceProvider.Status status);
    
    @Query("SELECT p FROM ServiceProvider p JOIN p.skills s WHERE s = :serviceType AND p.status = 'AVAILABLE'")
    List<ServiceProvider> findAvailableProvidersBySkill(@Param("serviceType") ServiceRequest.ServiceType serviceType);
    
    @Query("SELECT p FROM ServiceProvider p JOIN p.skills s WHERE s = :serviceType AND p.status = :status")
    List<ServiceProvider> findByStatusAndSkillsContaining(@Param("status") ServiceProvider.Status status, @Param("serviceType") ServiceRequest.ServiceType serviceType);
    
    List<ServiceProvider> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);
    
    boolean existsByEmail(String email);
}