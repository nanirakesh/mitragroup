package com.mitra.service;

import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public ServiceProvider registerProvider(ServiceProvider provider) {
        if (serviceProviderRepository.existsByEmail(provider.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return serviceProviderRepository.save(provider);
    }

    public List<ServiceProvider> findAllProviders() {
        return serviceProviderRepository.findAll();
    }

    public List<ServiceProvider> findAvailableProviders() {
        return serviceProviderRepository.findByStatus(ServiceProvider.Status.AVAILABLE);
    }

    public List<ServiceProvider> findAvailableProvidersBySkill(ServiceRequest.ServiceType serviceType) {
        return serviceProviderRepository.findAvailableProvidersBySkill(serviceType);
    }

    public Optional<ServiceProvider> findById(Long id) {
        return serviceProviderRepository.findById(id);
    }

    public Optional<ServiceProvider> findByEmail(String email) {
        return serviceProviderRepository.findByEmail(email);
    }

    public ServiceProvider save(ServiceProvider provider) {
        return serviceProviderRepository.save(provider);
    }

    public ServiceProvider updateStatus(Long providerId, ServiceProvider.Status status) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findById(providerId);
        if (providerOpt.isPresent()) {
            ServiceProvider provider = providerOpt.get();
            provider.setStatus(status);
            return serviceProviderRepository.save(provider);
        }
        throw new RuntimeException("Provider not found");
    }

    public void deleteProvider(Long id) {
        serviceProviderRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return serviceProviderRepository.existsByEmail(email);
    }
    
    public ServiceProvider updateProvider(ServiceProvider updatedProvider) {
        Optional<ServiceProvider> existingOpt = serviceProviderRepository.findById(updatedProvider.getId());
        if (existingOpt.isPresent()) {
            ServiceProvider existing = existingOpt.get();
            existing.setName(updatedProvider.getName());
            existing.setEmail(updatedProvider.getEmail());
            existing.setPhone(updatedProvider.getPhone());
            existing.setAddress(updatedProvider.getAddress());
            existing.setSkills(updatedProvider.getSkills());
            return serviceProviderRepository.save(existing);
        }
        throw new RuntimeException("Provider not found");
    }
}