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

    @Min(value = 1, message = "Weight must be at least 1 gram")
    double weightInGrams; // Khối lượng món ăn (gram)

    @NotNull(message = "Meal type is required")
    MealType mealType; // Loại bữa ăn (breakfast, lunch, dinner, snack)

    @NotNull(message = "Date is required")
    LocalDate date; // Ngày ăn (YYYY-MM-DD)
}

