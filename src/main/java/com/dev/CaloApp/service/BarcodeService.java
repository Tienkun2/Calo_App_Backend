//package com.dev.CaloApp.service;
//import com.dev.CaloApp.Enum.ErrorCode;
//import com.dev.CaloApp.dto.response.BarcodeCategoriesResponse;
//import com.dev.CaloApp.entity.Food;
//import com.dev.CaloApp.exception.AppException;
//import com.dev.CaloApp.mapper.FoodMapper;
//import com.dev.CaloApp.repository.FoodRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//@Service
//@Slf4j
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class BarcodeService {
//    RestTemplate restTemplate;
//    FoodService foodService;
//    FoodMapper foodMapper;
//
//    public BarcodeCategoriesResponse getProductByBarcode(String barcode) {
//        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
//        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(response.getBody());
//            if (!jsonNode.has("product")) {
//                throw new RuntimeException(new AppException(ErrorCode.BARCODE_NOT_IN_DATABASE));
//            }
//            JsonNode productNode = jsonNode.get("product");
//            JsonNode nutriscoreNode = productNode.get("nutriscore");
//            JsonNode nutrimentsNode = productNode.get("nutriments");
//            JsonNode nutriscoreDetails = nutriscoreNode.get("2021");
//            JsonNode nutriscoreDetailsData = nutriscoreDetails.get("data");
//
//            // Gộp lại thành node mới để deserialize
//            ((ObjectNode) productNode).setAll((ObjectNode) nutrimentsNode);
//
//            BarcodeCategoriesResponse result = objectMapper.treeToValue(nutriscoreDetailsData, BarcodeCategoriesResponse.class);
//            result.setName(productNode.get("product_name").asText());
//            result.setCategories(productNode.get("categories").asText());
//            result.setCarbs(nutrimentsNode.get("carbohydrates").asDouble());
//            result.setQuantity(productNode.get("quantity").asText());
//            result.calculateCaloriesPerServing();
//
//            try{
//                foodService.createFood(foodMapper.toFoodCreationRequest(result));
//            } catch (AppException e){
//                log.error(e.getMessage());
//            }
//            return result;
//        } catch (Exception e) {
//            throw new AppException((ErrorCode.BARCODE_NOT_IN_DATABASE));
////            throw new RuntimeException("Lỗi khi xử lý dữ liệu: " + e.getMessage(), e);
//        }
//    }
//}

package com.dev.CaloApp.service;

import com.dev.CaloApp.Enum.ErrorCode;
import com.dev.CaloApp.dto.request.FoodCreationRequest;
import com.dev.CaloApp.dto.response.BarcodeCategoriesResponse;
import com.dev.CaloApp.dto.response.FoodResponse;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.exception.AppException;
import com.dev.CaloApp.mapper.FoodMapper;
import com.dev.CaloApp.repository.FoodRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BarcodeService {
    RestTemplate restTemplate;
    FoodService foodService;
    FoodMapper foodMapper;
    FoodRepository foodRepository;

    public FoodResponse getProductByBarcode(String barcode) {
        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            if (!jsonNode.has("product")) {
                throw new AppException(ErrorCode.BARCODE_NOT_IN_DATABASE);
            }
            JsonNode productNode = jsonNode.get("product");
            JsonNode nutriscoreNode = productNode.get("nutriscore");
            JsonNode nutrimentsNode = productNode.get("nutriments");
            JsonNode nutriscoreDetails = nutriscoreNode.get("2021");
            JsonNode nutriscoreDetailsData = nutriscoreDetails.get("data");

            // Merge nutriments into product node for deserialization
            ((ObjectNode) productNode).setAll((ObjectNode) nutrimentsNode);

            // Map API data to BarcodeCategoriesResponse
            BarcodeCategoriesResponse barcodeResponse = objectMapper.treeToValue(nutriscoreDetailsData, BarcodeCategoriesResponse.class);
            barcodeResponse.setName(productNode.get("product_name").asText());
            barcodeResponse.setCategories(productNode.get("categories").asText());
            barcodeResponse.setCarbs(nutrimentsNode.get("carbohydrates").asDouble());
            barcodeResponse.setQuantity(productNode.get("quantity").asText());
            barcodeResponse.calculateCaloriesPerServing();

            // Map to FoodCreationRequest
            FoodCreationRequest foodCreationRequest = foodMapper.toFoodCreationRequest(barcodeResponse);

            // Check if food exists, create if not
            Food food;
            try {
                food = foodService.createFood(foodCreationRequest);
            } catch (AppException e) {
                if (e.getErrorCode() == ErrorCode.FOOD_EXISTED) {
                    // Food already exists, retrieve it
                    food = foodRepository.findByName(barcodeResponse.getName())
                            .orElseThrow(() -> new AppException(ErrorCode.FOOD_NOT_FOUND));
                } else {
                    throw e;
                }
            }

            // Map Food to FoodResponse
            return foodMapper.toFoodResponse(food);

        } catch (Exception e) {
            throw new AppException(ErrorCode.BARCODE_NOT_IN_DATABASE);
        }
    }
}
