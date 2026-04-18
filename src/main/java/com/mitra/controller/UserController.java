package com.mitra.controller;

import com.mitra.model.Location;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.LocationService;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private LocationService locationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        List<ServiceRequest> userRequests = serviceRequestService.findRequestsByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("requests", userRequests);
        return "user/dashboard";
    }

    @GetMapping("/request/new")
    public String showNewRequestForm(Model model) {
        model.addAttribute("serviceRequest", new ServiceRequest());
        model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
        model.addAttribute("priorities", ServiceRequest.Priority.values());
        return "user/new-request";
    }

    @PostMapping("/request/new")
    public String createRequest(@Valid @ModelAttribute("serviceRequest") ServiceRequest request,
                               BindingResult result, Authentication auth, HttpServletRequest httpRequest, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            model.addAttribute("priorities", ServiceRequest.Priority.values());
            return "user/new-request";
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);
        request.setUser(user);
        
        // Auto-detect location from IP if not provided
        if (request.getLatitude() == null || request.getLongitude() == null) {
            String clientIP = getClientIP(httpRequest);
            Location detectedLocation = locationService.getLocationFromIP(clientIP);
            
            if (detectedLocation != null) {
                request.setLatitude(String.valueOf(detectedLocation.getLatitude()));
                request.setLongitude(String.valueOf(detectedLocation.getLongitude()));
                
                if (request.getCity() == null) {
                    request.setCity(detectedLocation.getCity());
                }
            }
        }
        
        // Create request with auto-assignment
        ServiceRequest createdRequest = serviceRequestService.createRequest(request);
        
        return "redirect:/user/dashboard";
    }

    @GetMapping("/request/{id}")
    public String viewRequest(@PathVariable Long id, Model model, Authentication auth) {
        ServiceRequest request = serviceRequestService.findById(id).orElse(null);
        User user = userService.findByEmail(auth.getName()).orElse(null);
        
        if (request != null && request.getUser().equals(user)) {
            model.addAttribute("request", request);
            return "user/request-detail";
        }
        
        return "redirect:/user/dashboard";
    }
    
    @GetMapping("/requests")
    public String viewRequests(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElse(null);
        List<ServiceRequest> userRequests = serviceRequestService.findRequestsByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("requests", userRequests);
        return "user/requests";
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}