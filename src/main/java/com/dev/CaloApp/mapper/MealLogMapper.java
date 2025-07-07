package com.dev.CaloApp.mapper;

import com.dev.CaloApp.dto.request.FoodCreationRequest;
import com.dev.CaloApp.dto.request.MealLogCreationRequest;
import com.dev.CaloApp.dto.request.MealLogUpdateRequest;
import com.dev.CaloApp.dto.response.FoodResponse;
import com.dev.CaloApp.dto.response.MealLogResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MealLogMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "food", ignore = true)
    @Mapping(target = "totalCalories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MealLog toMealLog(MealLogCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "food", ignore = true)
    @Mapping(target = "totalCalories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateMealLog(@MappingTarget MealLog mealLog, MealLogUpdateRequest request);

    @Mapping(target = "mealType", expression = "java(mealLog.getMealType() != null ? mealLog.getMealType().name() : null)")
    MealLogResponse toMealLogResponse(MealLog mealLog);
}
