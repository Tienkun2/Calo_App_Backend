package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.UserCreationRequest;
import com.dev.CaloApp.dto.request.UserUpdateRequest;
import com.dev.CaloApp.dto.response.UserResponse;
import com.dev.CaloApp.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("create user success")
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUser() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("get all users success")
                .result(userService.getAllUser())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Cập nhật người dùng thành công")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa người dùng thành công")
                .build();
    }

    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo() {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("get user info success")
                .result(userService.getUserByInfo())
                .build();
    }

    @PutMapping("/updateMyInfo")
    public ApiResponse<UserResponse> updateUserInfo(@RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Cập nhật thông tin thành công")
                .result(userService.updateUserByInfo(request))
                .build();
    }

    @PostMapping("/update-daily")
    public ResponseEntity<String> updateWeightLostDaily() {
        userService.updateWeightLostDaily();
        return ResponseEntity.ok("Đã cập nhật số kg giảm hàng ngày cho tất cả người dùng.");
    }

    @GetMapping("/weight-lost/weekly")
    public ApiResponse<Map<String, Float>> getWeightLostWeekly() {
        return ApiResponse.<Map<String, Float>>builder()
                .code(1000)
                .message("get weekly weight lost success")
                .result(userService.getWeightLostWeekly())
                .build();
    }

    @GetMapping("/weight-lost/monthly")
    public ApiResponse<Map<String, Float>> getWeightLostMonthly() {
        return ApiResponse.<Map<String, Float>>builder()
                .code(1000)
                .message("get monthly weight lost success")
                .result(userService.getWeightLostMonthly())
                .build();
    }

    @GetMapping("/weight-lost")
    public ApiResponse<Map<String, Float>> getWeightLost() {
        return ApiResponse.<Map<String, Float>>builder()
                .code(1000)
                .message("get total weight lost success")
                .result(userService.getWeightLost())
                .build();
    }

    @DeleteMapping("/clear-data")
    public ApiResponse<UserResponse> clearDataUser() {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Xóa dữ liệu thành công")
                .result(userService.ClearDataUser())
                .build();
    }
}
