package com.dev.CaloApp.service;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.UserStepsRequest;
import com.dev.CaloApp.dto.response.UserStepsResponse;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.entity.UserSteps;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.mapper.UserStepsMapper;
import com.dev.CaloApp.repository.UserRepository;
import com.dev.CaloApp.repository.UserStepsRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserStepsService {
    UserStepsRepository userStepsRepository;
    UserRepository userRepository;
    UserStepsMapper userStepsMapper;

    private User getAuthenticatedUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private float calculateCalories(int steps, float weight) {
        return steps * (weight * 0.0005f);
    }

    public UserStepsResponse saveSteps(UserStepsRequest userStepsRequest) {
        User user = getAuthenticatedUser();
        float calories = calculateCalories(userStepsRequest.getSteps(), user.getWeight());

        // Kiểm tra xem user đã có dữ liệu trong ngày chưa
        UserSteps userSteps = userStepsRepository.findByUserIdAndDate(user.getId(), LocalDate.now());

        if (userSteps != null) {
            // Cập nhật số bước và calories nếu đã tồn tại
            userSteps.setSteps(userSteps.getSteps() + userStepsRequest.getSteps());
            userSteps.setCalories(userSteps.getCalories() + calories);
        } else {
            // Nếu chưa có, tạo mới
            userSteps = UserSteps.builder()
                    .user(user)
                    .date(LocalDate.now())
                    .steps(userStepsRequest.getSteps())
                    .calories(calories)
                    .build();
        }

        // Lưu lại vào DB
        userStepsRepository.save(userSteps);

        // Convert sang response
        return userStepsMapper.toUserStepsResponse(userSteps);
    }

    public UserStepsResponse getSteps(UserStepsRequest userStepsRequest) {
        User user = getAuthenticatedUser();
        UserSteps userSteps = userStepsRepository.findByUserIdAndDate(user.getId(), userStepsRequest.getDate());

        if (userSteps == null) {
            userSteps = UserSteps.builder()
                    .calories(0)
                    .steps(0)
                    .date(userStepsRequest.getDate())
                    .user(user)
                    .build();
        }

        return userStepsMapper.toUserStepsResponse(userSteps);
    }

    public double getTotalCaloriesLastNumberOfDays(int numberOfDays) {
        User user = getAuthenticatedUser();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(numberOfDays); // Lấy dữ liệu từ numberOfDays ngày trước

        List<UserSteps> userStepsList = userStepsRepository.findByUserIdAndDateBetween(user.getId(), startDate, today);

        // Tính tổng calories đã đốt
        return userStepsList.stream().mapToDouble(UserSteps::getCalories).sum();
    }

    public float getTotalCaloriesToday() {
        User user = getAuthenticatedUser();
        UserSteps userSteps = userStepsRepository.findByUserIdAndDate(user.getId(), LocalDate.now());
        return userSteps != null ? userSteps.getCalories() : 0.0f;
    }
}