package com.mitra.controller;

import com.mitra.model.Rating;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.repository.RatingRepository;
import com.mitra.repository.ServiceRequestRepository;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rating")
public class RatingController {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/add/{requestId}")
    public String showRatingForm(@PathVariable Long requestId, Model model, Authentication auth) {
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);
        if (request == null || !request.getStatus().equals(ServiceRequest.Status.COMPLETED)) {
            return "redirect:/user/dashboard";
        }
        
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user == null || !request.getUser().equals(user)) {
            return "redirect:/user/dashboard";
        }
        
        model.addAttribute("request", request);
        model.addAttribute("rating", new Rating());
        return "rating/add";
    }
    
    @PostMapping("/add/{requestId}")
    public String submitRating(@PathVariable Long requestId, 
                              @ModelAttribute Rating rating,
                              Authentication auth) {
        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);
        if (request == null || request.getAssignedProvider() == null) {
            return "redirect:/user/dashboard";
        }
        
        User user = userService.findByEmail(auth.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        
        rating.setServiceRequest(request);
        rating.setUser(user);
        rating.setProvider(request.getAssignedProvider());
        
        ratingRepository.save(rating);
        
        return "redirect:/user/dashboard?rated=true";
    }
}