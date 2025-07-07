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
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private int age;
    private float height;
    private float weight;
    private String gender;
    private String goal;
    private double dailyCalories;
    private int TDEE;
    private int BMR;
    private float BMI;
    private String state;
    private LocalDate createdate;
    private float caloDeficit;
    private float firstWeight;
} 