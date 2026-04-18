package com.mitra.controller;

import com.mitra.model.User;
import com.mitra.service.JwtService;
import com.mitra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = jwtService.generateToken(email, "ROLE_" + user.getRole().name());
                
                response.put("success", true);
                response.put("token", token);
                response.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getFirstName() + " " + user.getLastName(),
                    "role", user.getRole().name()
                ));
                
                return ResponseEntity.ok(response);
            }
            
        } catch (AuthenticationException e) {
            response.put("success", false);
            response.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
        
        response.put("success", false);
        response.put("error", "Login failed");
        return ResponseEntity.badRequest().body(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String email = jwtService.extractEmail(token);
                
                if (jwtService.validateToken(token, email)) {
                    Optional<User> userOpt = userService.findByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        response.put("valid", true);
                        response.put("user", Map.of(
                            "id", user.getId(),
                            "email", user.getEmail(),
                            "name", user.getFirstName() + " " + user.getLastName(),
                            "role", user.getRole().name()
                        ));
                        return ResponseEntity.ok(response);
                    }
                }
            }
        } catch (Exception e) {
            // Invalid token
        }
        
        response.put("valid", false);
        response.put("error", "Invalid token");
        return ResponseEntity.badRequest().body(response);
    }
}