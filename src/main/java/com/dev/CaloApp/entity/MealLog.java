package com.dev.CaloApp.entity;

import com.dev.CaloApp.Enum.MealType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "meal_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;  // ✅ Thay vì MealSession, ta lưu enum

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double weightInGrams; // Lượng thực phẩm (gram)

    private double totalCalories; // Tổng calo = (calories * quantity) / 100

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    // ✅ Tính tổng calo dựa trên thức ăn và khối lượng
    public void updateTotalCalories() {
        if (food != null && weightInGrams > 0) {
            this.totalCalories = (food.getCalories() * weightInGrams) / 100;
        } else {
            this.totalCalories = 0;
        }
    }

    public void setFood(Food food) {
        this.food = food;
        updateTotalCalories();
    }

    public void setWeightInGrams(double weightInGrams) {
        this.weightInGrams = weightInGrams;
        updateTotalCalories();
    }
}
