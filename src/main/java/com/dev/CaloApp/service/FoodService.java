package com.dev.CaloApp.service;


import com.dev.CaloApp.dto.request.FoodCreationRequest;
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
}
