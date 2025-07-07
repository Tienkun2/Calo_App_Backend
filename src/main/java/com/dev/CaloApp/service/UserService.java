package com.dev.CaloApp.service;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.UserCreationRequest;
import com.dev.CaloApp.dto.request.UserUpdateRequest;
import com.dev.CaloApp.dto.response.UserResponse;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.mapper.UserMapper;
import com.dev.CaloApp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    AuthenticationService authenticationService;
    MealLogService mealLogService;
    UserStepsService userStepsService;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        user.setPassword(encoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) {
        return userMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
        );
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getUserByInfo() {
        return userMapper.toUserResponse(getAuthentication());
    }

    public UserResponse updateUserByInfo(UserUpdateRequest request) {
        User user = getAuthentication();

        // Chỉ cập nhật các trường không null
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAge() > 0) {
            user.setAge(request.getAge());
        }
        if (request.getHeight() > 0) {
            user.setHeight(request.getHeight());
        }
        if (request.getWeight() > 0) {
            user.setWeight(request.getWeight());
        }
        if (request.getGoal() != null) {
            user.setGoal(request.getGoal());
        }

        if(request.getFirstWeight() > 0){
            user.setFirstWeight(request.getFirstWeight());
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    private User getAuthentication() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 0 * * *") // Chạy vào 00:00 AM mỗi ngày
    public void updateWeightLostDaily() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            float caloDeficit = (float) (user.getTDEE() - calculateTodayCalories(user)); // lượng calo thâm hụt
            float weightLost = 0;
            if (caloDeficit > 0) {
                weightLost = caloDeficit / 7700; // thâm hụt 7700 kcal -> giảm 1 cân
            } else {
                weightLost = caloDeficit / 7000; // dư 7000 -> tăng 1 cân
            }
            user.setWeight(user.getWeight() - weightLost);
            userRepository.save(user);
        }
        log.info("Đã cập nhật số kg giảm hàng ngày cho tất cả người dùng.");
    }

    public Map<String, Float> getWeightLostWeekly() {
        User user = getAuthentication();
        float caloDeficit = user.getTDEE() - calculateWeeklyCalories(); // lượng calo thâm hụt
        float weightLost = 0;
        if (caloDeficit > 0) {
            weightLost = caloDeficit / 7700; // thâm hụt 7700 kcal -> giảm 1 cân
        } else {
            weightLost = caloDeficit / 7000; // dư 7000 -> tăng 1 cân
        }
        // Trả về JSON
        Map<String, Float> response = new HashMap<>();
        response.put("WeightLost", weightLost);
        return response;
    }

    public Map<String, Float> getWeightLostMonthly() {
        User user = getAuthentication();
        float caloDeficit = user.getTDEE() - calculateMonthlyCalories(); // lượng calo thâm hụt
        float weightLost = 0;
        if (caloDeficit > 0) {
            weightLost = caloDeficit / 7700; // thâm hụt 7700 kcal -> giảm 1 cân
        } else {
            weightLost = caloDeficit / 7000; // dư 7000 -> tăng 1 cân
        }
        // Trả về JSON
        Map<String, Float> response = new HashMap<>();
        response.put("WeightLost", weightLost);
        return response;
    }

    public Map<String, Float> getWeightLost() {
        User user = getAuthentication();
        float weightLost = user.getWeight() - user.getFirstWeight();
        // Trả về JSON
        Map<String, Float> response = new HashMap<>();
        response.put("WeightLost", weightLost);
        return response;
    }

    private float calculateWeeklyCalories() {
        float totalCalories = (float) mealLogService.getTotalCaloriesLastNumberOfDays(7);
        float caloriesBurned = (float) userStepsService.getTotalCaloriesLastNumberOfDays(7);
        return (totalCalories - caloriesBurned);
    }

    private float calculateMonthlyCalories() {
        float totalCalories = (float) mealLogService.getTotalCaloriesLastNumberOfDays(30);
        float caloriesBurned = (float) userStepsService.getTotalCaloriesLastNumberOfDays(30);
        return (totalCalories - caloriesBurned);
    }

    private double calculateTodayCalories(User user) {
        LocalDate today = LocalDate.now();
        double totalCalories = mealLogService.getDailyCaloriesAtDay(user.getId(), today);
        double caloriesBurned = userStepsService.getTotalCaloriesToday();
        return (totalCalories - caloriesBurned);
    }


    public UserResponse ClearDataUser() {
        User user = getAuthentication();
        user.setWeight(0);
        user.setHeight(0);
        user.setAge(0);
        user.setTDEE(0);
        user.setBMR(0);
        user.setBMI(0);
        user.setState(null);
        user.setCaloDeficit(0);
        user.setFirstWeight(0);
        return userMapper.toUserResponse(userRepository.save(user));
    }

}