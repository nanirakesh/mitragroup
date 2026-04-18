package com.mitra.controller;

import com.mitra.model.Location;
import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.LocationService;
import com.mitra.service.ServiceProviderService;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private LocationService locationService;

    @PostMapping("/setup-test-data")
    public ResponseEntity<Map<String, Object>> setupTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create test user
            User testUser = new User();
            testUser.setFirstName("Test");
            testUser.setLastName("Customer");
            testUser.setEmail("customer@test.com");
            testUser.setPassword("password123");
            testUser.setRole(User.Role.USER);
            testUser = userService.save(testUser);
            
            // Create test provider in Mumbai
            ServiceProvider mumbaiProvider = new ServiceProvider();
            mumbaiProvider.setName("Mumbai Plumber");
            mumbaiProvider.setEmail("mumbai@provider.com");
            mumbaiProvider.setPhone("9876543210");
            mumbaiProvider.setSkills(Set.of(ServiceRequest.ServiceType.PLUMBING));
            mumbaiProvider.setStatus(ServiceProvider.Status.AVAILABLE);
            mumbaiProvider.setRating(4.5);
            mumbaiProvider.setAddress("Andheri, Mumbai");
            
            Location mumbaiLocation = new Location(19.0760, 72.8777, "Mumbai", "Maharashtra", "400001");
            mumbaiProvider.setLocation(mumbaiLocation);
            mumbaiProvider = serviceProviderService.save(mumbaiProvider);
            
            // Create test provider in Delhi
            ServiceProvider delhiProvider = new ServiceProvider();
            delhiProvider.setName("Delhi Electrician");
            delhiProvider.setEmail("delhi@provider.com");
            delhiProvider.setPhone("9876543211");
            delhiProvider.setSkills(Set.of(ServiceRequest.ServiceType.ELECTRICAL));
            delhiProvider.setStatus(ServiceProvider.Status.AVAILABLE);
            delhiProvider.setRating(4.0);
            delhiProvider.setAddress("CP, Delhi");
            
            Location delhiLocation = new Location(28.7041, 77.1025, "Delhi", "Delhi", "110001");
            delhiProvider.setLocation(delhiLocation);
            delhiProvider = serviceProviderService.save(delhiProvider);
            
            response.put("success", true);
            response.put("message", "Test data created successfully");
            response.put("testUser", Map.of(
                "id", testUser.getId(),
                "email", testUser.getEmail()
            ));
            response.put("providers", Map.of(
                "mumbai", Map.of("id", mumbaiProvider.getId(), "name", mumbaiProvider.getName()),
                "delhi", Map.of("id", delhiProvider.getId(), "name", delhiProvider.getName())
            ));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/create-request")
    public ResponseEntity<Map<String, Object>> createTestRequest(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get test user
            User testUser = userService.findByEmail("customer@test.com").orElse(null);
            if (testUser == null) {
                response.put("success", false);
                response.put("error", "Test user not found. Run /setup-test-data first");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create service request
            ServiceRequest request = new ServiceRequest();
            request.setTitle((String) requestData.get("title"));
            request.setDescription((String) requestData.get("description"));
            request.setServiceType(ServiceRequest.ServiceType.valueOf((String) requestData.get("serviceType")));
            request.setPriority(ServiceRequest.Priority.valueOf((String) requestData.get("priority")));
            request.setUser(testUser);
            request.setAddress((String) requestData.get("address"));
            request.setCity((String) requestData.get("city"));
            request.setPincode((String) requestData.get("pincode"));
            
            // Set coordinates if provided
            if (requestData.containsKey("latitude") && requestData.containsKey("longitude")) {
                request.setLatitude(String.valueOf(requestData.get("latitude")));
                request.setLongitude(String.valueOf(requestData.get("longitude")));
            }
            
            System.out.println("=== CREATING SERVICE REQUEST ===");
            System.out.println("Title: " + request.getTitle());
            System.out.println("Service Type: " + request.getServiceType());
            System.out.println("Location: " + request.getCity());
            System.out.println("Coordinates: " + request.getLatitude() + ", " + request.getLongitude());
            
            // Create request with auto-assignment
            ServiceRequest createdRequest = serviceRequestService.createRequest(request);
            
            System.out.println("=== AUTO-ASSIGNMENT RESULT ===");
            System.out.println("Request ID: " + createdRequest.getId());
            System.out.println("Status: " + createdRequest.getStatus());
            System.out.println("Assigned Provider: " + 
                (createdRequest.getAssignedProvider() != null ? 
                    createdRequest.getAssignedProvider().getName() : "NONE"));
            
            response.put("success", true);
            response.put("request", Map.of(
                "id", createdRequest.getId(),
                "title", createdRequest.getTitle(),
                "status", createdRequest.getStatus().toString(),
                "serviceType", createdRequest.getServiceType().toString(),
                "assignedProvider", createdRequest.getAssignedProvider() != null ? 
                    Map.of(
                        "id", createdRequest.getAssignedProvider().getId(),
                        "name", createdRequest.getAssignedProvider().getName(),
                        "phone", createdRequest.getAssignedProvider().getPhone()
                    ) : null
            ));
            
            if (createdRequest.getAssignedProvider() != null) {
                response.put("autoAssignment", "SUCCESS");
                response.put("message", "✅ Auto-assignment worked! Provider assigned: " + 
                    createdRequest.getAssignedProvider().getName());
            } else {
                response.put("autoAssignment", "FAILED");
                response.put("message", "❌ Auto-assignment failed - no suitable provider found");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-scenarios")
    public ResponseEntity<Map<String, Object>> getTestScenarios() {
        Map<String, Object> scenarios = new HashMap<>();
        
        scenarios.put("scenario1_mumbai_plumbing", Map.of(
            "title", "Fix kitchen sink",
            "description", "Kitchen sink is leaking badly",
            "serviceType", "PLUMBING",
            "priority", "HIGH",
            "address", "Andheri West, Mumbai",
            "city", "Mumbai",
            "pincode", "400058",
            "latitude", 19.1136,
            "longitude", 72.8697,
            "expectedResult", "Should assign Mumbai Plumber (nearby + matching skill)"
        ));
        
        scenarios.put("scenario2_delhi_electrical", Map.of(
            "title", "Power outage repair",
            "description", "No electricity in bedroom",
            "serviceType", "ELECTRICAL", 
            "priority", "URGENT",
            "address", "Connaught Place, Delhi",
            "city", "Delhi",
            "pincode", "110001",
            "latitude", 28.7041,
            "longitude", 77.1025,
            "expectedResult", "Should assign Delhi Electrician (nearby + matching skill)"
        ));
        
        scenarios.put("scenario3_mumbai_electrical", Map.of(
            "title", "Electrical work in Mumbai",
            "description", "Need electrical repair",
            "serviceType", "ELECTRICAL",
            "priority", "MEDIUM", 
            "address", "Bandra, Mumbai",
            "city", "Mumbai",
            "pincode", "400050",
            "latitude", 19.0596,
            "longitude", 72.8295,
            "expectedResult", "Should NOT assign (no electrical provider in Mumbai)"
        ));
        
        return ResponseEntity.ok(scenarios);
    }
    
    @PostMapping("/complete-request/{requestId}")
    public ResponseEntity<Map<String, Object>> completeRequest(@PathVariable Long requestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ServiceRequest request = serviceRequestService.findById(requestId).orElse(null);
            if (request == null) {
                response.put("success", false);
                response.put("error", "Request not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Mark as completed
            request.setStatus(ServiceRequest.Status.COMPLETED);
            request.setActualCost(500.0); // Set a test cost
            ServiceRequest updatedRequest = serviceRequestService.updateRequest(request);
            
            response.put("success", true);
            response.put("message", "Request marked as completed. Payment can now be processed.");
            response.put("paymentUrl", "/payment/request/" + requestId);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}