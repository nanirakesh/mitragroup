package com.mitra.controller;

import com.mitra.model.ServiceRequest;
import com.mitra.model.ServiceProvider;
import com.mitra.model.User;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.ServiceProviderService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<ServiceRequest> allRequests = serviceRequestService.findAllRequests();
        List<ServiceProvider> allProviders = serviceProviderService.findAllProviders();
        List<User> allUsers = userService.findAllUsers();
        
        stats.put("totalRequests", allRequests.size());
        stats.put("totalProviders", allProviders.size());
        stats.put("totalUsers", allUsers.size());
        stats.put("pendingRequests", allRequests.stream().filter(r -> r.getStatus() == ServiceRequest.Status.PENDING).count());
        stats.put("completedRequests", allRequests.stream().filter(r -> r.getStatus() == ServiceRequest.Status.COMPLETED).count());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/providers/nearby")
    public ResponseEntity<List<ServiceProvider>> getNearbyProviders(
            @RequestParam String latitude,
            @RequestParam String longitude,
            @RequestParam(defaultValue = "10") Double radius) {
        
        List<ServiceProvider> providers = serviceProviderService.findAllProviders();
        return ResponseEntity.ok(providers);
    }
    
    @PostMapping("/requests/{id}/track")
    public ResponseEntity<Map<String, String>> trackRequest(@PathVariable Long id) {
        Map<String, String> tracking = new HashMap<>();
        tracking.put("status", "IN_PROGRESS");
        tracking.put("estimatedArrival", "30 minutes");
        tracking.put("providerLocation", "12.9716,77.5946");
        
        return ResponseEntity.ok(tracking);
    }
}