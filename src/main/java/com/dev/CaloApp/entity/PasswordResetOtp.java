//package com.dev.CaloApp.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(name = "password_reset_tokens")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PasswordResetToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String token;
//
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Column(nullable = false)
//    private LocalDateTime expiryDate;
//
//    // Token mặc định hết hạn sau 1 giờ
//    public static PasswordResetToken createToken(User user) {
//        return PasswordResetToken.builder()
//                .token(UUID.randomUUID().toString())
//                .user(user)
//                .expiryDate(LocalDateTime.now().plusHours(1))
//                .build();
//    }
//
//    public boolean isExpired() {
//        return LocalDateTime.now().isAfter(expiryDate);
//    }
//}



package com.dev.CaloApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "password_reset_otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp; // OTP code

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // OTP hết hạn sau 10 phút
    public static PasswordResetOtp createOtp(User user) {
        return PasswordResetOtp.builder()
                .otp(generateOtp())
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    private static String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Tạo OTP 6 chữ số
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
