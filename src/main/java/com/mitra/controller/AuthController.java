package com.mitra.controller;

import com.mitra.model.User;
import com.mitra.model.OtpToken;
import com.mitra.service.UserService;
import com.mitra.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OtpService otpService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "auth/register";
        }

        try {
            // Store user data in session temporarily
            user.setRole(User.Role.USER);
            user.setEmailVerified(false);
            
            // Send OTP for email verification
            otpService.generateOtp(user.getEmail(), OtpToken.OtpType.EMAIL_VERIFICATION);
            
            // Store user data temporarily (don't save to DB yet)
            model.addAttribute("email", user.getEmail());
            model.addAttribute("firstName", user.getFirstName());
            model.addAttribute("lastName", user.getLastName());
            model.addAttribute("phone", user.getPhone());
            model.addAttribute("address", user.getAddress());
            model.addAttribute("password", user.getPassword());
            
            return "auth/verify-email";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/verify-email")
    public String showVerifyEmail(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-email";
    }
    
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, @RequestParam String otp,
                             @RequestParam String firstName, @RequestParam String lastName,
                             @RequestParam String phone, @RequestParam String address,
                             @RequestParam String password, Model model) {
        if (otpService.verifyOtp(email, otp, OtpToken.OtpType.EMAIL_VERIFICATION)) {
            // Create and save user after OTP verification
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setAddress(address);
            user.setPassword(password);
            user.setRole(User.Role.USER);
            user.setEmailVerified(true);
            
            userService.registerUser(user);
            model.addAttribute("success", "Email verified successfully! Please login.");
            return "auth/login";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            model.addAttribute("firstName", firstName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            model.addAttribute("password", password);
            return "auth/verify-email";
        }
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "auth/forgot-password";
    }
    
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        if (userService.existsByEmail(email)) {
            otpService.generateOtp(email, OtpToken.OtpType.PASSWORD_RESET);
            model.addAttribute("email", email);
            return "auth/reset-password";
        } else {
            model.addAttribute("error", "Email not found");
            return "auth/forgot-password";
        }
    }
    
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp, 
                               @RequestParam String newPassword, Model model) {
        if (otpService.verifyOtp(email, otp, OtpToken.OtpType.PASSWORD_RESET)) {
            userService.resetPassword(email, newPassword);
            model.addAttribute("success", "Password reset successfully! Please login.");
            return "auth/login";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            return "auth/reset-password";
        }
    }
}