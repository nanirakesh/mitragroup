package com.mitra.repository;

import com.mitra.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmailAndOtpAndTypeAndUsedFalse(String email, String otp, OtpToken.OtpType type);
    void deleteByEmailAndType(String email, OtpToken.OtpType type);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}