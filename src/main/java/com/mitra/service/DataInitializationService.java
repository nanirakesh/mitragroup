package com.mitra.service;

import com.mitra.model.User;
import com.mitra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeTestUser();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            admin.setEmailVerified(true);
            
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail);
        }
    }
    
    private void initializeTestUser() {
        String testEmail = "test@gmail.com";
        if (!userRepository.existsByEmail(testEmail)) {
            User testUser = new User();
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setRole(User.Role.USER);
            testUser.setEnabled(true);
            testUser.setEmailVerified(true);
            testUser.setPhone("+91 9876543210");
            testUser.setAddress("123 Test Street, Test City");
            
            userRepository.save(testUser);
            System.out.println("Test user created: " + testEmail);
        }
    }
}