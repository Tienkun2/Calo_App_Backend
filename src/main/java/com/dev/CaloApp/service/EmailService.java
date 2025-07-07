package com.dev.CaloApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Calo App - Đặt lại mật khẩu");
        message.setText("Xin chào,\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhập mã otp dưới đây để đặt lại mật khẩu của bạn:\n\n"
                + resetLink + "\n\n"
                + "Otp này sẽ hết hạn sau 24 giờ.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.");
        mailSender.send(message);
    }
}