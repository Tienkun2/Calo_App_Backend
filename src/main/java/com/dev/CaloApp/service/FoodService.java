package com.dev.CaloApp.service;


import com.dev.CaloApp.dto.request.FoodCreationRequest;
import com.dev.CaloApp.dto.request.FoodPredictRequest;
import com.dev.CaloApp.dto.response.FoodPredictResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.mapper.FoodMapper;
import com.dev.CaloApp.repository.FoodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class FoodService {
    @Autowired
    FoodRepository foodRepository;
    @Autowired
    FoodMapper foodMapper;
    @Autowired
    GeminiService geminiService;

    public Food createFood(FoodCreationRequest request){
        if(!(foodRepository.findByName(request.getName()).isEmpty())){
            throw new AppException(ErrorCode.FOOD_EXISTED);
        }
        Food food = foodMapper.toFood(request);
        return foodRepository.save(food);
    }

    public List<Food> getAllFood(){
        return foodRepository.findAll();
    }

    public Food getFood(Long id){
        return foodRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
    }

    public List<Food> getRandomFoods() {
        return foodRepository.findRandomFoods();
    }


    public Food updateFood(Long id, FoodCreationRequest request) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
        foodMapper.updateFood(food,request);
        return foodRepository.save(food);
    }

    public void deleteFood(Long id){
        foodRepository.deleteById(id);
    }


    public List<Food> searchFoodByName(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of(); // Trả về danh sách rỗng nếu từ khóa trống
        }
        List<Food> food = foodRepository.searchFoodName(keyword);
        if (food.isEmpty()){
            food.add(foodRepository.save(geminiService.generateNewFood(keyword)));
        }
        return food;
    }

    public void saveFoodsIfNotExist(List<Food> foods) {
        for (Food food : foods) {
            Optional<Food> existingFood = foodRepository.findByName(food.getName());
            if (existingFood.isEmpty()) {
                foodRepository.save(food);
            }
        }
    }


    public void saveFoodsByMealLogIfNotExist(List<MealLog> mealLogs) {
        for (MealLog mealLog : mealLogs) {
            Food food = mealLog.getFood();
            // Tìm món ăn theo tên
            Optional<Food> existingFood = foodRepository.findByName(food.getName());
            if (existingFood.isPresent()) {
                // Gán Food đã tồn tại (có id) vào MealLog
                mealLog.setFood(existingFood.get());
            } else {
                // Lưu Food mới và gán vào MealLog
                Food savedFood = foodRepository.save(food);
                mealLog.setFood(savedFood);
            }
        }
    }

    public FoodPredictResponse predictFood(FoodPredictRequest request) {
        String content = request.getContent();
        if (!StringUtils.hasText(content)) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Parse content to extract food name and quantity
        String[] parts = content.trim().split("\\s+", 2);
        if (parts.length < 2) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        String quantityStr = parts[0];
        String foodName = parts[1];

        // Extract quantity and unit
        double quantity = 100.0; // default
        String unit = "g"; // default
        boolean foundInMaster = false;

        try {
            // Try to parse quantity (e.g., "100g", "50ml", "1")
            if (quantityStr.matches("\\d+(\\.\\d+)?[a-zA-Z]+")) {
                // Extract number and unit
                String numberPart = quantityStr.replaceAll("[a-zA-Z]+", "");
                String unitPart = quantityStr.replaceAll("\\d+(\\.\\d+)?", "");
                quantity = Double.parseDouble(numberPart);
                unit = unitPart;
            } else if (quantityStr.matches("\\d+(\\.\\d+)?")) {
                quantity = Double.parseDouble(quantityStr);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, use defaults
            quantity = 100.0;
            unit = "g";
        }

        // Search for existing food in database
        List<Food> existingFoods = foodRepository.searchFoodName(foodName);
        Food food;
        
        if (!existingFoods.isEmpty()) {
            food = existingFoods.get(0);
            foundInMaster = true;
        } else {
            // Generate new food using Gemini service
            food = geminiService.generateNewFood(foodName);
            // Save the new food to database
            food = foodRepository.save(food);
        }

        // Calculate nutrition for the specified quantity
        double factor = quantity / 100.0; // Convert to per 100g basis
        double calories = food.getCalories() * factor;
        double protein = food.getProtein() * factor;
        double carbs = food.getCarbs() * factor;
        double fat = food.getFat() * factor;
        double fiber = food.getFiber() * factor;

        // Build nutrition info
        FoodPredictResponse.NutritionInfo nutritionInfo = FoodPredictResponse.NutritionInfo.builder()
                .protein(protein)
                .carbs(carbs)
                .fat(fat)
                .fiber(fiber)
                .build();

        // Build response
        return FoodPredictResponse.builder()
                .success(true)
                .found_in_master(foundInMaster)
                .food(foodName)
                .quantity(quantity)
                .unit(unit)
                .calories(calories)
                .calories_per_100g(food.getCalories())
                .weight_g(quantity)
                .nutrition_info(nutritionInfo)
                .build();
    }
}
