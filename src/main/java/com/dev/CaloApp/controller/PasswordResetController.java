package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.PasswordResetRequest;
import com.dev.CaloApp.dto.request.VerifyOtpRequest;
import com.dev.CaloApp.dto.response.PasswordResetResponse;
import com.dev.CaloApp.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // Gửi OTP đến emailA
    @PostMapping("/request-reset")
    public ResponseEntity<PasswordResetResponse> requestReset(
            @Valid @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(request));
    }

    // Xác thực OTP và đặt lại mật khẩu
    @PostMapping("/verify-otp")
    public ResponseEntity<PasswordResetResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(passwordResetService.verifyOtpAndResetPassword(request));
    }
}
