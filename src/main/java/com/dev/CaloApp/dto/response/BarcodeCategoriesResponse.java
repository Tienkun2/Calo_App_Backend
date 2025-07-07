package com.dev.CaloApp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class BarcodeCategoriesResponse {
    @JsonProperty("product_name")
    private String name;
    @JsonProperty("energy")
    private Double totalCalories;
    @JsonProperty("proteins")
    private Double protein;
    @JsonProperty("fiber")
    private Double fiber;
    @JsonProperty("sodium")
    private Double sodium;
    @JsonProperty("sugars")
    private Double sugars;
    @JsonProperty("saturated_fat")
    private Double fat;
    @JsonProperty("carbohydrates") // thường carbs là carbohydrates
    private Double carbs;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("categories")
    private String categories;
    private Double calories_per_serving; // calories / 100 g / 100 ml
    private String servingSize = "100g"; // 100g, 100ml, ...

    public void calculateCaloriesPerServing() {
        if (totalCalories == null || quantity == null) return;

        try {
            String[] parts = quantity.trim().split("\\s+");
            double quantityValue = 1;
            String unit = "";

            if (parts.length == 2) {
                quantityValue = Double.parseDouble(parts[0]);
                unit = parts[1].toLowerCase();
            } else if (parts.length == 1) {
                String numericPart = parts[0].replaceAll("[^0-9.]", "");
                String unitPart = parts[0].replaceAll("[0-9.]", "");
                quantityValue = Double.parseDouble(numericPart);
                unit = unitPart.toLowerCase();
            }
            log.info("" + quantityValue + unit);
            switch (unit) {
                case "ml":
                case "g":
                    break;
                case "liter":
                case "l":
                case "kg":
                    quantityValue *= 1000;
                    break;
                default:
                    calories_per_serving = 0.0;
                    return;
            }

            calories_per_serving = totalCalories / quantityValue * 100;
            servingSize = 100 + unit;
        } catch (Exception e) {
            calories_per_serving = 0.0;
        }
    }
}
