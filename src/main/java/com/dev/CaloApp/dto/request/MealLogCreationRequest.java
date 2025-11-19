package com.dev.CaloApp.dto.request;

import com.dev.CaloApp.Enum.MealType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealLogCreationRequest {
    @NotNull(message = "Food ID is required")
    Long foodId; // ID của món ăn

    private String foodName; // Tên món ăn (optional, dùng để tạo food mới nếu foodId không tồn tại)
    
    // Thông tin nutrition của food (optional, dùng để tạo food mới với đầy đủ thông tin)
    private Double foodCalories; // Calories per 100g
    private Double foodProtein;  // Protein (g)
    private Double foodFat;      // Fat (g)
    private Double foodCarbs;    // Carbs (g)
    private Double foodFiber;    // Fiber (g)
    private String foodServingSize; // Serving size (default: "100g")

    @Min(value = 1, message = "Weight must be at least 1 gram")
    double weightInGrams; // Khối lượng món ăn (gram)

    @NotNull(message = "Meal type is required")
    MealType mealType; // Loại bữa ăn (breakfast, lunch, dinner, snack)

    @NotNull(message = "Date is required")
    LocalDate date; // Ngày ăn (YYYY-MM-DD)
}

