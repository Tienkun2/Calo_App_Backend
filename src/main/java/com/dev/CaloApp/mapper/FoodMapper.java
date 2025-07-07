package com.dev.CaloApp.mapper;
import com.dev.CaloApp.dto.request.FoodCreationRequest;
import com.dev.CaloApp.dto.response.BarcodeCategoriesResponse;
import com.dev.CaloApp.dto.response.FoodResponse;
import com.dev.CaloApp.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
// Chuyển đổi giữa Entity - DTO
public interface FoodMapper {
    Food toFood(FoodCreationRequest request);
    void updateFood(@MappingTarget Food food, FoodCreationRequest request);
    @Mapping(source = "calories_per_serving", target = "calories")
    FoodCreationRequest toFoodCreationRequest(BarcodeCategoriesResponse barcodeCategoriesResponse);

    // New method to map Food to FoodResponse
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "calories", target = "calories")
    @Mapping(source = "protein", target = "protein")
    @Mapping(source = "fat", target = "fat")
    @Mapping(source = "carbs", target = "carbs")
    @Mapping(source = "fiber", target = "fiber")
    @Mapping(source = "servingSize", target = "servingSize")
    FoodResponse toFoodResponse(Food food);
}
