package com.mitra.controller;

import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.ServiceProviderService;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/requests")
    public ResponseEntity<Map<String, Object>> getUserRequests(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findByEmail(auth.getName());
            if (userOpt.isPresent()) {
                List<ServiceRequest> requests = serviceRequestService.findRequestsByUser(userOpt.get());
                response.put("success", true);
                response.put("requests", requests);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @GetMapping("/worker/requests")
    public ResponseEntity<Map<String, Object>> getWorkerRequests(Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
            if (providerOpt.isPresent()) {
                List<ServiceRequest> requests = serviceRequestService.findRequestsByProvider(providerOpt.get());
                response.put("success", true);
                response.put("requests", requests);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/worker/request/{id}/status")
    public ResponseEntity<Map<String, Object>> updateRequestStatus(@PathVariable Long id,
                                                                 @RequestBody Map<String, String> statusUpdate,
                                                                 Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
            Optional<ServiceRequest> requestOpt = serviceRequestService.findById(id);
            
            if (providerOpt.isPresent() && requestOpt.isPresent()) {
                ServiceRequest request = requestOpt.get();
                ServiceProvider provider = providerOpt.get();
                
                // Verify request is assigned to this provider
                if (request.getAssignedProvider() != null && 
                    request.getAssignedProvider().getId().equals(provider.getId())) {
                    
                    ServiceRequest.Status newStatus = ServiceRequest.Status.valueOf(statusUpdate.get("status"));
                    serviceRequestService.updateStatus(id, newStatus);
                    
                    response.put("success", true);
                    response.put("message", "Status updated successfully");
                    return ResponseEntity.ok(response);
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/user/request")
    public ResponseEntity<Map<String, Object>> createRequest(@RequestBody ServiceRequest request,
                                                           Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findByEmail(auth.getName());
            if (userOpt.isPresent()) {
                request.setUser(userOpt.get());
                ServiceRequest createdRequest = serviceRequestService.createRequest(request);
                
                response.put("success", true);
                response.put("request", createdRequest);
                response.put("message", "Request created successfully");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.badRequest().body(response);
    }
}