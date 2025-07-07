package com.dev.CaloApp.repository;

import com.dev.CaloApp.entity.PasswordResetOtp;
import com.dev.CaloApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findByOtp(String otp);
    Optional<PasswordResetOtp> findByUser(User user);

    @Modifying
    @Query("DELETE FROM PasswordResetOtp p WHERE p.expiryDate < :now")
    void deleteAllExpiredOtps(LocalDateTime now);
}
