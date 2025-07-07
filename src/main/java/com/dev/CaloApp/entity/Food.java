package com.dev.CaloApp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private String servingSize = "100g"; // 100 g / 100 ml / 1 cái / ...

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
