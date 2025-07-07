package com.dev.CaloApp.repository;

import com.dev.CaloApp.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    @Query("SELECT new map(DATE(m.createdAt) as date, SUM(m.totalCalories) as totalCalories) " +
            "FROM MealLog m WHERE m.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(m.createdAt)")
    List<Map<String, Object>> getTotalCaloriesPerDay(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    List<MealLog> findByUserIdAndCreatedAt(Long userId, LocalDate date);
    List<MealLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Optional<MealLog> findByUserId(Long userId);
}
