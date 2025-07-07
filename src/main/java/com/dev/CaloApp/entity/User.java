package com.dev.CaloApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private int age; // Tuổi
    private float height; // Chiều cao (cm)
    private float weight; // Cân nặng (kg)
    private String gender; // "male" hoặc "female"
    private String goal = "LOSE_WEIGHT"; // Mục tiêu: GAIN_MUSCLE, LOSE_WEIGHT, MAINTAIN_WEIGHT

    private double dailyCalories; // Lượng calo khuyến nghị mỗi ngày
    private int TDEE; // Tổng tiêu hao năng lượng hàng ngày
    private int BMR; // Chỉ số trao đổi chất cơ bản
    private float BMI; // Chỉ số khối cơ thể
    private String state; //Tình trạng cân nặng dựa trên BMI
    private String googleId;
    private String provider;
    private String providerId;

    private LocalDate createdate = LocalDate.now();
    private float caloDeficit = 500; //calo cần thâm hụt hằng ngày
    private float firstWeight;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<MealLog> mealLogs;

    // 🔥 Tự động tính toán các chỉ số trước khi lưu vào DB
    @PrePersist
    @PreUpdate
    public void calculateMetrics() {
        caloDeficit = 500;
        goal = "LOSE_WEIGHT";
        if (height > 0 && weight > 0 && age > 0) {
            // 1️⃣ Tính BMR
            if ("Nam".equalsIgnoreCase(gender)) {
                this.BMR = (int) (10 * weight + 6.25 * height - 5 * age + 5);
            } else if ("Nu".equalsIgnoreCase(gender)) {
                this.BMR = (int) (10 * weight + 6.25 * height - 5 * age - 161);
            } else {
                this.BMR = 0; // Nếu giới tính không hợp lệ
            }

            // 2️⃣ Tính BMI
//            < 18.5	Gầy
//            18.5 - 24.9	Bình thường
//            25 - 29.9	Thừa cân
//            30 - 34.9	Béo phì cấp độ 1
//            35 - 39.9	Béo phì cấp độ 2
//            ≥ 40	Béo phì cấp độ 3
            this.BMI = (float) (weight / Math.pow(height / 100, 2));
            if (BMI < 18.5){
                state = "Gầy";
                goal = "GAIN_MUSCLE";
                caloDeficit = -500;
            }
            else if (BMI < 24.9){
                state = "Bình thường";
                goal = "MAINTAIN_WEIGHT";
                caloDeficit = 0;
            }
            else if (BMI < 29.9) state = "Thừa cân";
            else if (BMI < 34.9) state = "Béo phì cấp độ 1";
            else if (BMI < 39.9) state = "Béo phì cấp độ 2";
            else if (BMI >= 40) state = "Béo phì cấp độ 3";


            // 3️⃣ Tính TDEE (giả định mức độ hoạt động trung bình)
//            double activityLevel = 1.55; // Hoạt động trung bình
            this.TDEE = (int) (this.BMR * 1.375);

            // 4️⃣ Tính dailyCalories theo mục tiêu
            switch (goal) {
                case "LOSE_WEIGHT": // Giảm cân
                this.dailyCalories = this.TDEE - 500;
                    break;
                case "GAIN_MUSCLE": // Tăng cân
                    this.dailyCalories = this.TDEE + 500;
                    break;
                default: // Duy trì cân nặng
                    this.dailyCalories = this.TDEE;
                    break;
            }
        } else {
            // Nếu dữ liệu không hợp lệ, đặt giá trị mặc định
            this.BMR = 0;
            this.BMI = 0;
            this.TDEE = 0;
            this.dailyCalories = 0;
        }
    }
}