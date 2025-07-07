package com.dev.CaloApp.service;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.ChangePasswordRequest;
import com.dev.CaloApp.dto.request.PasswordResetRequest;
import com.dev.CaloApp.dto.request.VerifyOtpRequest;
import com.dev.CaloApp.dto.response.PasswordResetResponse;
import com.dev.CaloApp.entity.PasswordResetOtp;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.repository.PasswordResetOtpRepository;
import com.dev.CaloApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;

    @Transactional
    public PasswordResetResponse requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        // Xóa OTP cũ nếu có
        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        // Tạo OTP mới
        PasswordResetOtp otp = PasswordResetOtp.createOtp(user);
        otpRepository.save(otp);

        // Gửi email chứa OTP
        emailService.sendPasswordResetEmail(user.getEmail(), "Your OTP: " + otp.getOtp());

        return PasswordResetResponse.builder()
                .success(true)
                .message("OTP has been sent to your email")
                .build();
    }

    @Transactional
    public PasswordResetResponse verifyOtpAndResetPassword(VerifyOtpRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // Kiểm tra OTP
        PasswordResetOtp otp = otpRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        if (otp.isExpired()) {
            otpRepository.delete(otp);
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        // Đổi mật khẩu
        User user = otp.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi sử dụng
        otpRepository.delete(otp);

        return PasswordResetResponse.builder()
                .success(true)
                .message("Password has been reset successfully")
                .build();
    }

    // Scheduled task để xóa OTP hết hạn
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteAllExpiredOtps(LocalDateTime.now());
    }
}
