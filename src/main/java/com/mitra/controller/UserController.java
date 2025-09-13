package com.mitra.controller;

import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.service.ServiceRequestService;
import com.mitra.service.UserService;
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
                               BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("serviceTypes", ServiceRequest.ServiceType.values());
            model.addAttribute("priorities", ServiceRequest.Priority.values());
            return "user/new-request";
        }

        User user = userService.findByEmail(auth.getName()).orElse(null);
        request.setUser(user);
        serviceRequestService.createRequest(request);
        
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
}