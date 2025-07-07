package com.dev.CaloApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealLogResponse {
    private Long id;
    private String mealType;
    private FoodResponse food;
    private Double weightInGrams;
    private Double totalCalories;
    private LocalDate createdAt;
}
