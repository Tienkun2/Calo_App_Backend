package com.dev.CaloApp.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodPredictResponse {
    private boolean success;
    private boolean found_in_master;
    private String food;
    private double quantity;
    private String unit;
    private double calories;
    private double calories_per_100g;
    private double weight_g;
    private NutritionInfo nutrition_info;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NutritionInfo {
        private double protein;
        private double carbs;
        private double fat;
        private double fiber;
    }
}
