package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.MealLogCreationRequest;
import com.dev.CaloApp.dto.request.MealLogUpdateRequest;
import com.dev.CaloApp.dto.response.MealLogResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.service.AuthenticationService;
import com.dev.CaloApp.service.MealLogService;
import com.google.protobuf.Api;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meal-log")
public class MealLogController {

    private static final Log log = LogFactory.getLog(MealLogController.class);
    @Autowired
    private MealLogService mealLogService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/create")
    public ApiResponse<MealLogResponse> createMealLog(
            @Valid @RequestBody MealLogCreationRequest request) {
        MealLogResponse mealLogResponse = mealLogService.createMealLog(request);
        return new ApiResponse<>(200, "Meal log created successfully", mealLogResponse);
    }

    @GetMapping("")
    public ApiResponse<Map<String,Object>> getAllMealLog(
            @RequestParam(value = "date", required = false) String dateStr){
        LocalDate date = (dateStr != null) ? LocalDate.parse(dateStr) : LocalDate.now();
        return new ApiResponse<>(200, "Get all meal logs successfully", mealLogService.getAllMealLog(date));
    }

    @PutMapping("/{mealLogId}")
    public ApiResponse<MealLogResponse> updateMealLog(
            @PathVariable Long mealLogId,
            @Valid @RequestBody MealLogUpdateRequest request) {
        MealLogResponse updatedMealLog = mealLogService.updateMealLog(mealLogId, request);
        return new ApiResponse<>(200, "Meal log updated successfully", updatedMealLog);
    }

    @DeleteMapping("/{mealLogId}")
    public ApiResponse<Map<String, Double>> deleteMealLog(
            @PathVariable Long mealLogId,
            @RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        Map<String, Double> response = mealLogService.deleteMealLog(mealLogId, date);
        return new ApiResponse<>(200, "Meal log deleted successfully", response);
    }

    @GetMapping(value = "/suggest-meal")
    public ApiResponse<List<MealLog>> suggestMeal() {
//        List<MealLog> suggestedMeals = mealLogService.suggestMeal();
//        // In id của các món ăn từ database
//        suggestedMeals.forEach(mealLog ->
//                System.out.println("Food Name: " + mealLog.getFood().getName() +
//                        ", Food ID: " + mealLog.getFood().getId()));
        return ApiResponse.<List<MealLog>>builder()
                .result(mealLogService.suggestMeal())
                .code(1000)
                .build();
    }

    @GetMapping(value = "/suggest-meal-with-food")
    public ApiResponse<List<Food>> suggestMealWithFood(@RequestBody List<String> foodNames) {
        log.info("Suggesting meal with food names: " + foodNames);
        return ApiResponse.<List<Food>>builder()
                .result(mealLogService.suggestMealWithFood(foodNames))
                .code(1000)
                .build();
    }
}