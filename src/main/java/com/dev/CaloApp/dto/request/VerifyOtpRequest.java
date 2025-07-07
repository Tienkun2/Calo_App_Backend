package com.dev.CaloApp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
