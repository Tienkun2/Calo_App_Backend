package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.UserStepsRequest;
import com.dev.CaloApp.dto.response.UserStepsResponse;
import com.dev.CaloApp.service.UserStepsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/steps")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserStepsController {
    UserStepsService userStepsService;

    // Lấy thông tin User theo ID
    @PostMapping
    public ApiResponse<UserStepsResponse> saveSteps(@RequestBody UserStepsRequest userStepsRequest) {
        return ApiResponse.<UserStepsResponse>builder()
                .result(userStepsService.saveSteps(userStepsRequest))
                .code(1000)
                .build();
    }
    @GetMapping
    public ApiResponse<UserStepsResponse> getSteps(@RequestBody UserStepsRequest userStepsRequest) {
        return ApiResponse.<UserStepsResponse>builder()
                .result(userStepsService.getSteps(userStepsRequest))
                .code(1000)
                .build();
    }
}
