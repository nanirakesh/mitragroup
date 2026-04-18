package com.mitra.controller;

import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.service.ServiceProviderService;
import com.mitra.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
        
        if (providerOpt.isEmpty()) {
            return "redirect:/login?error=provider_not_found";
        }
        
        ServiceProvider provider = providerOpt.get();
        List<ServiceRequest> assignedRequests = serviceRequestService.findRequestsByProvider(provider);
        
        // Count requests by status
        long pendingCount = assignedRequests.stream().filter(r -> r.getStatus() == ServiceRequest.Status.ASSIGNED).count();
        long inProgressCount = assignedRequests.stream().filter(r -> r.getStatus() == ServiceRequest.Status.IN_PROGRESS).count();
        long completedCount = assignedRequests.stream().filter(r -> r.getStatus() == ServiceRequest.Status.COMPLETED).count();
        
        model.addAttribute("provider", provider);
        model.addAttribute("assignedRequests", assignedRequests);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        
        return "worker/dashboard";
    }

    @GetMapping("/request/{id}")
    public String viewRequest(@PathVariable Long id, Model model, Authentication auth) {
        Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
        Optional<ServiceRequest> requestOpt = serviceRequestService.findById(id);
        
        if (providerOpt.isEmpty() || requestOpt.isEmpty()) {
            return "redirect:/worker/dashboard";
        }
        
        ServiceRequest request = requestOpt.get();
        ServiceProvider provider = providerOpt.get();
        
        // Check if request is assigned to this provider
        if (request.getAssignedProvider() == null || !request.getAssignedProvider().getId().equals(provider.getId())) {
            return "redirect:/worker/dashboard";
        }
        
        model.addAttribute("request", request);
        model.addAttribute("provider", provider);
        model.addAttribute("statuses", ServiceRequest.Status.values());
        
        return "worker/request-detail";
    }

    @PostMapping("/request/{id}/update-status")
    public String updateRequestStatus(@PathVariable Long id, 
                                    @RequestParam ServiceRequest.Status status,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
            Optional<ServiceRequest> requestOpt = serviceRequestService.findById(id);
            
            if (providerOpt.isEmpty() || requestOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Request not found");
                return "redirect:/worker/dashboard";
            }
            
            ServiceRequest request = requestOpt.get();
            ServiceProvider provider = providerOpt.get();
            
            // Verify request is assigned to this provider
            if (request.getAssignedProvider() == null || !request.getAssignedProvider().getId().equals(provider.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/worker/dashboard";
            }
            
            // Update request status
            ServiceRequest updatedRequest = serviceRequestService.updateStatus(id, status);
            
            // Update provider status based on request status
            if (status == ServiceRequest.Status.IN_PROGRESS) {
                provider.setStatus(ServiceProvider.Status.BUSY);
            } else if (status == ServiceRequest.Status.COMPLETED) {
                provider.setStatus(ServiceProvider.Status.AVAILABLE);
            }
            serviceProviderService.save(provider);
            
            redirectAttributes.addFlashAttribute("success", "Status updated successfully!");
            return "redirect:/worker/request/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
            return "redirect:/worker/request/" + id;
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        Optional<ServiceProvider> providerOpt = serviceProviderService.findByEmail(auth.getName());
        
        if (providerOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        model.addAttribute("provider", providerOpt.get());
        return "worker/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute ServiceProvider provider,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<ServiceProvider> existingProviderOpt = serviceProviderService.findByEmail(auth.getName());
            
            if (existingProviderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Provider not found");
                return "redirect:/worker/profile";
            }
            
            ServiceProvider existingProvider = existingProviderOpt.get();
            
            // Update only allowed fields
            existingProvider.setName(provider.getName());
            existingProvider.setPhone(provider.getPhone());
            existingProvider.setAddress(provider.getAddress());
            
            serviceProviderService.save(existingProvider);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/worker/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/worker/profile";
        }
    }
}