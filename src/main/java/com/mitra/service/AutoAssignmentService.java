package com.mitra.service;

import com.mitra.model.Location;
import com.mitra.model.ServiceRequest;
import com.mitra.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AutoAssignmentService {
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private LocationService locationService;
    
    /**
     * Auto-assign pending requests every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void autoAssignPendingRequests() {
        List<ServiceRequest> pendingRequests = serviceRequestRepository.findByStatus(ServiceRequest.Status.PENDING);
        
        for (ServiceRequest request : pendingRequests) {
            try {
                serviceRequestService.autoAssignProvider(request.getId());
                System.out.println("Auto-assigned provider for request ID: " + request.getId());
            } catch (Exception e) {
                System.out.println("Failed to auto-assign request ID: " + request.getId() + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * Detect and set location for new requests
     */
    @Async
    public void detectAndSetLocation(ServiceRequest request, String clientIP) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            Location detectedLocation = locationService.getLocationFromIP(clientIP);
            
            if (detectedLocation != null) {
                request.setLatitude(String.valueOf(detectedLocation.getLatitude()));
                request.setLongitude(String.valueOf(detectedLocation.getLongitude()));
                
                if (request.getCity() == null) {
                    request.setCity(detectedLocation.getCity());
                }
                if (request.getPincode() == null) {
                    request.setPincode(detectedLocation.getPincode());
                }
                
                serviceRequestRepository.save(request);
            }
        }
    }
    
    /**
     * Get client IP address from request
     */
    public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}