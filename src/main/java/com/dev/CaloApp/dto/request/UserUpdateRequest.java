package com.dev.CaloApp.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @Size(max = 100, message = "NAME_TOO_LONG")
    private String name;

    @Pattern(regexp = "Nam|Nu", message = "GENDER_INVALID")
    private String gender;

//    @Positive(message = "AGE_POSITIVE")
//    @Min(value = 1, message = "AGE_MIN")
    @Max(value = 150, message = "AGE_MAX")
    private int age;

//    @Positive(message = "HEIGHT_POSITIVE")
//    @DecimalMin(value = "50.0", message = "HEIGHT_MIN")
    @Max(value = 300, message = "HEIGHT_MAX")
    @Digits(integer = 3, fraction = 1, message = "HEIGHT_DECIMAL")
    private float height;

//    @Positive(message = "WEIGHT_POSITIVE")
//    @DecimalMin(value = "20.0", message = "WEIGHT_MIN")
    @Max(value = 500, message = "WEIGHT_MAX")
    @Digits(integer = 3, fraction = 1, message = "WEIGHT_DECIMAL")
    private float weight;

    // Thêm trường firstWeight
//    @Positive(message = "FIRST_WEIGHT_POSITIVE")
//    @DecimalMin(value = "20.0", message = "FIRST_WEIGHT_MIN")
    @Max(value = 500, message = "FIRST_WEIGHT_MAX")
    @Digits(integer = 3, fraction = 1, message = "FIRST_WEIGHT_DECIMAL")
    private float firstWeight;

//    @NotBlank(message = "GOAL_NOT_BLANK")
//    @Pattern(regexp = "LOSE_WEIGHT|GAIN_MUSCLE|MAINTAIN_WEIGHT", message = "GOAL_INVALID")
    private String goal;
}