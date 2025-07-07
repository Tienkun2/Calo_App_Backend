package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.AuthenticationRequest;
import com.dev.CaloApp.dto.request.InstrospectRequest;
import com.dev.CaloApp.dto.response.AuthenticationResponse;
import com.dev.CaloApp.dto.response.IntrospectResponse;
import com.dev.CaloApp.service.AuthenticationService;
import com.dev.CaloApp.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspected(@RequestBody InstrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping()
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
