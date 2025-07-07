package com.dev.CaloApp.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MealLogUpdateRequest {
    @NotNull(message = "Date is required")
    LocalDate date; // Ngày ăn (YYYY-MM-DD)

    @NotNull(message = "Food ID is required")
    Long foodId; // ID của món ăn

    @Min(value = 1, message = "Weight must be at least 1 gram")
    double weightInGrams; // Khối lượng món ăn (gram)

}
