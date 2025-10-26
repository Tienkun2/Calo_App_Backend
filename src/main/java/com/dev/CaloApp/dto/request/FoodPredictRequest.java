package com.dev.CaloApp.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodPredictRequest {
    private String content;
}
