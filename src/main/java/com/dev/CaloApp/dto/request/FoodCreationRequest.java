package com.dev.CaloApp.dto.request;

import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodCreationRequest {
    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false)
    private double calories; // Calo trên 100g

    @Column(nullable = false)
    private double protein;  // Đạm (g)

    @Column(nullable = false)
    private double fat;      // Chất béo (g)

    @Column(nullable = false)
    private double carbs;    // Tinh bột (g)

    @Column(nullable = false)
    private double fiber;    // Chất xơ (g)

    @Column(nullable = false, length = 50)
    private String servingSize; // 100 g / 100 ml / 1 cái / ...
}
