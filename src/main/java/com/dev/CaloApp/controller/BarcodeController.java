package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.response.BarcodeCategoriesResponse;
import com.dev.CaloApp.dto.response.FoodResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.service.BarcodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/barcode")
@AllArgsConstructor
public class BarcodeController {
    private final BarcodeService barcodeService;

    @GetMapping("/{barcode}")
    public ApiResponse<FoodResponse> getProductByBarcode(@PathVariable String barcode) {
        ApiResponse<FoodResponse> apiResponse = new ApiResponse<>();
        FoodResponse foodResponse = barcodeService.getProductByBarcode(barcode);
        return ApiResponse.<FoodResponse>builder()
                .code(200)
                .message("Get product by barcode success")
                .result(foodResponse)
                .build();
    }
}
