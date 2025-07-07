package com.dev.CaloApp.controller;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.FoodCreationRequest;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.repository.UserRepository;
import com.dev.CaloApp.service.FoodService;
import com.dev.CaloApp.service.GeminiService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FoodController
{
    FoodService foodService;
    GeminiService geminiService;
    UserRepository userRepository;

    @PostMapping()
    public ApiResponse<Food> createFood(@RequestBody FoodCreationRequest request){
        ApiResponse apiResponse = new ApiResponse();
        return ApiResponse.<Food>builder()
                .code(200)
                .message("create food success")
                .result(foodService.createFood(request))
                .build();
    }

    @GetMapping()
    public ApiResponse<List<Food>> getAllFood(){
        ApiResponse apiResponse = new ApiResponse();
        return ApiResponse.<List<Food>>builder()
                .result(foodService.getAllFood())
                .code(200)
                .message("get all food success")
                .build();
    }

    @GetMapping("/random")
    public ApiResponse<List<Food>> getRandomFood(){
        ApiResponse apiResponse = new ApiResponse();
        return ApiResponse.<List<Food>>builder()
                .code(200)
                .result(foodService.getRandomFoods())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Food> getFood(@PathVariable Long id){
        ApiResponse apiResponse = new ApiResponse();
        return ApiResponse.<Food>builder()
                .message("Get food by id " + id + " " + "success")
                .code(200)
                .result(foodService.getFood(id))
                .build();
    }

//    @GetMapping("/search")
//    public List<Food> searchFood(@RequestParam String keyword) {
//        System.out.println("🔍 Received request: keyword = " + keyword);
//        List<Food> foods = foodService.searchFoodByName(keyword);
//        System.out.println("🍽 Found " + foods.size() + " foods");
//        return foods;
//    }

    @GetMapping("/search")
    public ApiResponse<List<Food>> seacrhFood(@RequestParam String keyword){
        ApiResponse apiResponse = new ApiResponse();
        System.out.println("🔍 Received request: keyword = " + keyword);
        return ApiResponse.<List<Food>>builder()
                .code(200)
                .result(foodService.searchFoodByName(keyword))
                .message("search food success")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Food> deleteFood(@PathVariable Long id){
        ApiResponse apiResponse = new ApiResponse();
        foodService.deleteFood(id);
        return ApiResponse.<Food>builder()
                .code(200)
                .message("delete food success")
                .build();
    }
}
