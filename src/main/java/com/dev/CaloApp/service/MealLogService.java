package com.dev.CaloApp.service;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.MealLogCreationRequest;
import com.dev.CaloApp.dto.request.MealLogUpdateRequest;
import com.dev.CaloApp.dto.response.FoodResponse;
import com.dev.CaloApp.dto.response.MealLogResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.mapper.MealLogMapper;
import com.dev.CaloApp.repository.FoodRepository;
import com.dev.CaloApp.repository.MealLogRepository;
import com.dev.CaloApp.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MealLogService {
     MealLogRepository mealLogRepository;
     FoodRepository foodRepository;
     UserRepository userRepository;
     GeminiService geminiService;
     FoodService foodService;
     MealLogMapper mealLogMapper;

    public MealLogResponse createMealLog(@Valid MealLogCreationRequest request) {
        User user = getAuthentication();
        var food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));

        MealLog mealLog = mealLogMapper.toMealLog(request);
        mealLog.setUser(user);
        mealLog.setFood(food);
        mealLog.setTotalCalories(food.getCalories() * request.getWeightInGrams() / 100.0);
        mealLog.setCreatedAt(request.getDate());

        return mealLogMapper.toMealLogResponse(mealLogRepository.save(mealLog));
    }

    public Map<String, Object> getAllMealLog(LocalDate date) {
        User user = getAuthentication();
        List<MealLogResponse> mealLogs = mealLogRepository.findByUserIdAndCreatedAt(user.getId(), date)
                .stream().map(mealLogMapper::toMealLogResponse).collect(Collectors.toList());

        return Map.of(
                "totalDailyCalories", mealLogs.stream().mapToDouble(MealLogResponse::getTotalCalories).sum(),
                "mealLogs", mealLogs
        );
    }

    public double getDailyCaloriesAtDay(Long userId, LocalDate date) {
        log.info("Fetching meal logs for userId: {} and date: {}", userId, date);
        List<MealLogResponse> mealLogs = mealLogRepository.findByUserIdAndCreatedAt(userId, date)
                .stream()
                .map(mealLogMapper::toMealLogResponse)
                .collect(Collectors.toList());
        double totalCalories = mealLogs.stream().mapToDouble(MealLogResponse::getTotalCalories).sum();
        return totalCalories;
    }

    public double getTotalCaloriesFromDate(LocalDate startDate) {
        User user = getAuthentication();
        LocalDate today = LocalDate.now();

        List<MealLogResponse> mealLogs = mealLogRepository.findByUserIdAndCreatedAtBetween(user.getId(), startDate, today)
                .stream().map(mealLogMapper::toMealLogResponse).collect(Collectors.toList());

        return mealLogs.stream().mapToDouble(MealLogResponse::getTotalCalories).sum();
    }

    public double getTotalCalories() {
        User user = getAuthentication();
        List<MealLogResponse> mealLogs = mealLogRepository.findByUserId(user.getId())
                .stream().map(mealLogMapper::toMealLogResponse).collect(Collectors.toList());

        return mealLogs.stream().mapToDouble(MealLogResponse::getTotalCalories).sum();
    }

    public double getTotalCaloriesLastNumberOfDays(int numberOfDay) {
        User user = getAuthentication();
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(numberOfDay);

        List<MealLogResponse> mealLogs = mealLogRepository.findByUserIdAndCreatedAtBetween(user.getId(), sevenDaysAgo, today)
                .stream().map(mealLogMapper::toMealLogResponse).collect(Collectors.toList());

        return mealLogs.stream().mapToDouble(MealLogResponse::getTotalCalories).sum();
    }

    @Transactional
    public MealLogResponse updateMealLog(Long mealLogId, @Valid MealLogUpdateRequest request) {
        User user = getAuthentication();
        MealLog mealLog = getUserMealLog(mealLogId, user.getId());
        
        mealLogMapper.updateMealLog(mealLog, request);
        mealLog.setTotalCalories(mealLog.getFood().getCalories() * request.getWeightInGrams() / 100.0);
        
        return mealLogMapper.toMealLogResponse(mealLogRepository.save(mealLog));
    }

    @Transactional
    public Map<String, Double> deleteMealLog(Long mealLogId, LocalDate date) {
        User user = getAuthentication();
        MealLog mealLog = getUserMealLog(mealLogId, user.getId());
        mealLogRepository.delete(mealLog);

        List<MealLog> mealLogsOfDay = mealLogRepository.findByUserIdAndCreatedAt(user.getId(), date);

        return Map.of(
                "updatedMealCalories", mealLogsOfDay.stream()
                        .filter(m -> m.getMealType().equals(mealLog.getMealType()))
                        .mapToDouble(MealLog::getTotalCalories)
                        .sum(),
                "updatedDailyCalories", mealLogsOfDay.stream()
                        .mapToDouble(MealLog::getTotalCalories)
                        .sum()
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private MealLog getUserMealLog(Long mealLogId, Long userId) {
        MealLog mealLog = mealLogRepository.findById(mealLogId)
                .orElseThrow(() -> new AppException(ErrorCode.MEAL_LOG_NOT_FOUND));
        if (!mealLog.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.MEAL_LOG_NOT_FOUND);
        }
        return mealLog;
    }

    public List<MealLog> suggestMeal() {
        User user = getAuthentication();
        List<MealLog> suggestedMealLog = geminiService.generateMenu(user.getDailyCalories());
        foodService.saveFoodsByMealLogIfNotExist(suggestedMealLog);
        return suggestedMealLog;
    }

    public List<Food> suggestMealWithFood(List<String> foodNames) {
        List<Food> foods = new ArrayList<>();
        for (var name : foodNames) {
            try {
                foods.add(foodRepository.findByName(name).orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND)));
            } catch (AppException e) {
                log.info(name + ": " + e.getMessage());
            }
        }

        List<Food> foodList = geminiService.generateMenuWithFood(foods);
        foodService.saveFoodsIfNotExist(foodList);
        return foodList;
    }

    private User getAuthentication() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}