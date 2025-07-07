package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.GoogleLoginRequest;
import com.dev.CaloApp.dto.response.GoogleLoginResponse;
import com.dev.CaloApp.service.GoogleAuthService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RequestMapping("/auth")
public class GoogleAuthController {

    @Autowired
    GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<GoogleLoginResponse>> authenticateWithGoogle(@RequestBody GoogleLoginRequest request) {
        ApiResponse<GoogleLoginResponse> response = googleAuthService.authenticateWithGoogle(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
