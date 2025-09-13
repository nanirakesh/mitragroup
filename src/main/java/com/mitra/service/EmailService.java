package com.mitra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@mitra.com}")
    private String fromEmail;
    
    public void sendEmail(String to, String subject, String text) {
        try {
            // Send real email using App Password
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(fromEmail);
            
            mailSender.send(message);
            
            // Log success
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("==============================");
            
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Still log OTP for backup
            System.out.println("=== EMAIL FAILED - OTP INFO ===");
            System.out.println("To: " + to);
            System.out.println("Message: " + text);
            System.out.println("==============================");
        }
    }
}