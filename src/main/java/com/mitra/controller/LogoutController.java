package com.mitra.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {
    
    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
    
    @GetMapping("/logout")
    public String logoutGet() {
        return "redirect:/login?logout";
    }
}