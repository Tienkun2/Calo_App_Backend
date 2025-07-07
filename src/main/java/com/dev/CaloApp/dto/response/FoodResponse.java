package com.dev.CaloApp.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double fiber;
    private String servingSize;
}
