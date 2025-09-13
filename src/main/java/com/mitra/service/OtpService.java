package com.mitra.service;

import com.mitra.model.OtpToken;
import com.mitra.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OtpService {
    
    @Autowired
    private OtpTokenRepository otpTokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    private final SecureRandom random = new SecureRandom();
    
    public String generateOtp(String email, OtpToken.OtpType type) {
        // Delete existing OTPs for this email and type
        otpTokenRepository.deleteByEmailAndType(email, type);
        
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));
        
        // Save OTP token
        OtpToken otpToken = new OtpToken(email, otp, type);
        otpTokenRepository.save(otpToken);
        
        // Send email with app information
        String subject = type == OtpToken.OtpType.EMAIL_VERIFICATION ? 
            "Welcome to Mitra - Verify Your Email" : "Mitra - Reset Your Password";
        
        String message = createEmailMessage(otp, type);
        
        // For development: Display OTP in console
        System.out.println("\n*** OTP GENERATED ***");
        System.out.println("Email: " + email);
        System.out.println("OTP: " + otp);
        System.out.println("Type: " + type);
        System.out.println("*********************\n");
        
        emailService.sendEmail(email, subject, message);
        
        return otp;
    }
    
    public boolean verifyOtp(String email, String otp, OtpToken.OtpType type) {
        Optional<OtpToken> tokenOpt = otpTokenRepository
            .findByEmailAndOtpAndTypeAndUsedFalse(email, otp, type);
        
        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();
            if (!token.isExpired()) {
                token.setUsed(true);
                otpTokenRepository.save(token);
                return true;
            }
        }
        return false;
    }
    
    public void cleanupExpiredTokens() {
        otpTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
    
    private String createEmailMessage(String otp, OtpToken.OtpType type) {
        StringBuilder message = new StringBuilder();
        
        if (type == OtpToken.OtpType.EMAIL_VERIFICATION) {
            message.append("Welcome to Mitra - Your Home Service Management Platform!\n\n");
            message.append("Thank you for registering with Mitra. To complete your registration, please verify your email address.\n\n");
        } else {
            message.append("Mitra - Password Reset Request\n\n");
            message.append("You have requested to reset your password for your Mitra account.\n\n");
        }
        
        message.append("Your One-Time Password (OTP): ").append(otp).append("\n\n");
        message.append("This OTP is valid for 10 minutes only.\n\n");
        
        message.append("About Mitra Services:\n");
        message.append("• Home Cleaning Services\n");
        message.append("• Plumbing & Electrical Repairs\n");
        message.append("• Carpentry & Painting\n");
        message.append("• Appliance Repair & Maintenance\n");
        message.append("• Professional Service Providers\n");
        message.append("• 24/7 Customer Support\n\n");
        
        message.append("Visit us: http://65.2.171.45:8080\n");
        message.append("For support: admin@mitra.com\n\n");
        
        message.append("If you didn't request this, please ignore this email.\n\n");
        message.append("Best regards,\n");
        message.append("Team Mitra");
        
        return message.toString();
    }
}