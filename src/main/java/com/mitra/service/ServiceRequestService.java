package com.mitra.service;

import com.mitra.model.ServiceRequest;
import com.mitra.model.ServiceProvider;
import com.mitra.model.User;
import com.mitra.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceProviderService serviceProviderService;

    public ServiceRequest createRequest(ServiceRequest request) {
        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> findAllRequests() {
        return serviceRequestRepository.findAll();
    }

    public List<ServiceRequest> findRequestsByUser(User user) {
        return serviceRequestRepository.findByUser(user);
    }

    public List<ServiceRequest> findRequestsByStatus(ServiceRequest.Status status) {
        return serviceRequestRepository.findByStatus(status);
    }

    public List<ServiceRequest> findUnassignedRequests() {
        return serviceRequestRepository.findByAssignedProviderIsNull();
    }

    public Optional<ServiceRequest> findById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    public ServiceRequest assignProvider(Long requestId, Long providerId) {
        Optional<ServiceRequest> requestOpt = serviceRequestRepository.findById(requestId);
        Optional<ServiceProvider> providerOpt = serviceProviderService.findById(providerId);

        if (requestOpt.isPresent() && providerOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();
            ServiceProvider provider = providerOpt.get();

            request.setAssignedProvider(provider);
            request.setStatus(ServiceRequest.Status.ASSIGNED);
            provider.setStatus(ServiceProvider.Status.BUSY);

            serviceProviderService.save(provider);
            return serviceRequestRepository.save(request);
        }
        throw new RuntimeException("Request or Provider not found");
    }

    public ServiceRequest autoAssignProvider(Long requestId) {
        Optional<ServiceRequest> requestOpt = serviceRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();
            List<ServiceProvider> availableProviders = serviceProviderService
                .findAvailableProvidersBySkill(request.getServiceType());

            if (!availableProviders.isEmpty()) {
                ServiceProvider bestProvider = availableProviders.stream()
                    .max((p1, p2) -> Double.compare(p1.getRating(), p2.getRating()))
                    .orElse(availableProviders.get(0));

                return assignProvider(requestId, bestProvider.getId());
            }
        }
        throw new RuntimeException("No available providers found");
    }

    public ServiceRequest updateStatus(Long requestId, ServiceRequest.Status status) {
        Optional<ServiceRequest> requestOpt = serviceRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();
            request.setStatus(status);

            if (status == ServiceRequest.Status.COMPLETED && request.getAssignedProvider() != null) {
                ServiceProvider provider = request.getAssignedProvider();
                provider.setStatus(ServiceProvider.Status.AVAILABLE);
                provider.setCompletedJobs(provider.getCompletedJobs() + 1);
                serviceProviderService.save(provider);
            }

            return serviceRequestRepository.save(request);
        }
        throw new RuntimeException("Request not found");
    }

    public void deleteRequest(Long id) {
        serviceRequestRepository.deleteById(id);
    }
}