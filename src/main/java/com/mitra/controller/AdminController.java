package com.mitra.controller;

import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.ServiceProviderService;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<ServiceRequest> allRequests = serviceRequestService.findAllRequests();
        List<ServiceRequest> unassignedRequests = serviceRequestService.findUnassignedRequests();
        List<ServiceProvider> allProviders = serviceProviderService.findAllProviders();
        List<ServiceProvider> availableProviders = serviceProviderService.findAvailableProviders();

        model.addAttribute("totalRequests", allRequests.size());
        model.addAttribute("unassignedRequests", unassignedRequests.size());
        model.addAttribute("totalProviders", allProviders.size());
        model.addAttribute("availableProviders", availableProviders.size());
        model.addAttribute("recentRequests", allRequests.stream().limit(5).toList());

        return "admin/dashboard";
    }

    @GetMapping("/requests")
    public String viewAllRequests(Model model) {
        List<ServiceRequest> requests = serviceRequestService.findAllRequests();
        model.addAttribute("requests", requests);
        return "admin/requests";
    }

    @GetMapping("/request/{id}")
    public String viewRequest(@PathVariable Long id, Model model) {
        ServiceRequest request = serviceRequestService.findById(id).orElse(null);
        if (request != null) {
            List<ServiceProvider> availableProviders = serviceProviderService
                .findAvailableProvidersBySkill(request.getServiceType());
            model.addAttribute("request", request);
            model.addAttribute("availableProviders", availableProviders);
            model.addAttribute("statuses", ServiceRequest.Status.values());
        }
        return "admin/request-detail";
    }

    @PostMapping("/request/{id}/assign")
    public String assignProvider(@PathVariable Long id, @RequestParam Long providerId) {
        try {
            serviceRequestService.assignProvider(id, providerId);
        } catch (Exception e) {
            // Handle error
        }
        return "redirect:/admin/request/" + id;
    }

    @PostMapping("/request/{id}/auto-assign")
    public String autoAssignProvider(@PathVariable Long id) {
        try {
            serviceRequestService.autoAssignProvider(id);
        } catch (Exception e) {
            // Handle error
        }
        return "redirect:/admin/request/" + id;
    }

    @PostMapping("/request/{id}/status")
    public String updateRequestStatus(@PathVariable Long id, @RequestParam ServiceRequest.Status status) {
        try {
            serviceRequestService.updateStatus(id, status);
        } catch (Exception e) {
            // Handle error
        }
        return "redirect:/admin/request/" + id;
    }

    @GetMapping("/providers")
    public String viewAllProviders(Model model) {
        List<ServiceProvider> providers = serviceProviderService.findAllProviders();
        model.addAttribute("providers", providers);
        return "admin/providers";
    }

    @GetMapping("/provider/new")
    public String showNewProviderForm(Model model) {
        model.addAttribute("provider", new ServiceProvider());
        model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
        return "admin/new-provider";
    }

    @PostMapping("/provider/new")
    public String createProvider(@Valid @ModelAttribute("provider") ServiceProvider provider,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            return "admin/new-provider";
        }

        try {
            serviceProviderService.registerProvider(provider);
            return "redirect:/admin/providers";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            return "admin/new-provider";
        }
    }

    @GetMapping("/provider/{id}")
    public String viewProvider(@PathVariable Long id, Model model) {
        ServiceProvider provider = serviceProviderService.findById(id).orElse(null);
        if (provider != null) {
            model.addAttribute("provider", provider);
            return "admin/provider-detail";
        }
        return "redirect:/admin/providers";
    }
    
    @GetMapping("/provider/{id}/edit")
    public String editProvider(@PathVariable Long id, Model model) {
        ServiceProvider provider = serviceProviderService.findById(id).orElse(null);
        if (provider != null) {
            model.addAttribute("provider", provider);
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            return "admin/edit-provider";
        }
        return "redirect:/admin/providers";
    }
    
    @PostMapping("/provider/{id}/edit")
    public String updateProvider(@PathVariable Long id, 
                                @Valid @ModelAttribute("provider") ServiceProvider provider,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            return "admin/edit-provider";
        }
        
        try {
            provider.setId(id);
            serviceProviderService.updateProvider(provider);
            return "redirect:/admin/provider/" + id;
        } catch (Exception e) {
            model.addAttribute("error", "Update failed: " + e.getMessage());
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            return "admin/edit-provider";
        }
    }

    @PostMapping("/provider/{id}/status")
    public String updateProviderStatus(@PathVariable Long id, @RequestParam ServiceProvider.Status status) {
        try {
            serviceProviderService.updateStatus(id, status);
        } catch (Exception e) {
            // Handle error
        }
        return "redirect:/admin/providers";
    }
    
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
}