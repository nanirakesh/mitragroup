package com.mitra.controller;

import com.mitra.model.User;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.findByEmail(email).orElse(null);
        
        if (user != null) {
            model.addAttribute("user", user);
            switch (user.getRole()) {
                case ADMIN:
                    return "redirect:/admin/dashboard";
                case PROVIDER:
                    return "redirect:/provider/dashboard";
                default:
                    return "redirect:/user/dashboard";
            }
        }
        return "redirect:/login";
    }
}
