package com.dev.CaloApp.service;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.GeminiRequest;
import com.dev.CaloApp.entity.Food;
import com.dev.CaloApp.entity.MealLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiService {

    @Value("${spring.google.gemini.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ApiResponse<String> generateContent(GeminiRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<GeminiRequest> requestHttpEntity = new HttpEntity<>(request, headers);
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestHttpEntity, String.class);
            // Parse JSON và lấy nội dung phản hồi
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String replyText = jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText(); // Chỉ lấy nội dung text

            return ApiResponse.<String>builder()
                    .code(200)
                    .message("success")
                    .result(replyText) // Chỉ trả về nội dung text
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error calling Gemini: " + e.getMessage())
                    .build();
        }
    }

    public List<MealLog> generateMenu(double tdee) {
        try {
            String promptText = "Hãy tạo một thực đơn cho 1 ngày với tổng lượng calo khoảng " + tdee + " kcal. Trả về danh sách thực phẩm dưới dạng JSON theo mẫu sau:\n" +
                    "{\n" +
                    "    \"mealType\": \"DINNER\",\n" +
                    "    \"food\": {\n" +
                    "        \"name\": \"Cua\",\n" +
                    "        \"calories\": 87.0,\n" +
                    "        \"protein\": 19.4,\n" +
                    "        \"fat\": 1.3,\n" +
                    "        \"carbs\": 0.0,\n" +
                    "        \"fiber\": 0.0\n" +
                    "    },\n" +
                    "    \"weightInGrams\": 250.0,\n" +
                    "    \"totalCalories\": 217.5\n" +
                    "}\n" +
                    "Yêu cầu: \n" +
                    "- Bao gồm các bữa: BREAKFAST, LUNCH, DINNER, SNACK.\n" +
                    "- Mỗi món ăn cần có các thông tin: name, calories, protein, fat, carbs, fiber.\n" +
                    "- Cung cấp số gram của mỗi món và tổng calo tính theo khẩu phần.\n" +
                    "- Tổng lượng calo của cả ngày khoảng " + tdee + " kcal.\n" +
                    "- Định dạng đầu ra phải là JSON hợp lệ, không có giải thích thêm.";

            log.info(tdee + "");
            // ✅ Tạo GeminiRequest đúng định dạng
            GeminiRequest request = new GeminiRequest(
                    List.of(new GeminiRequest.Content("user", List.of(new GeminiRequest.Part(promptText))))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiRequest> requestHttpEntity = new HttpEntity<>(request, headers);

            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestHttpEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String foodJson = jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
            if (foodJson.startsWith("```")) {
                int firstNewLine = foodJson.indexOf("\n");
                int lastBacktick = foodJson.lastIndexOf("```");
                if (firstNewLine != -1 && lastBacktick != -1 && lastBacktick > firstNewLine) {
                    foodJson = foodJson.substring(firstNewLine + 1, lastBacktick).trim();
                }
            }
            return objectMapper.readValue(foodJson, new TypeReference<List<MealLog>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi Gemini API: " + e.getMessage(), e);
        }
    }

    public List<Food> generateMenuWithFood(List<Food> foods) {
        try {
            String promptText = "Dưới đây là danh sách các món ăn chính:\n" +
                    foods.stream()
                            .map(food -> String.format("{\n    \"name\": \"%s\",\n    \"calories\": %.1f,\n    \"protein\": %.1f,\n    \"fat\": %.1f,\n    \"carbs\": %.1f,\n    \"fiber\": %.1f\n}",
                                    food.getName(), food.getCalories(), food.getProtein(), food.getFat(), food.getCarbs(), food.getFiber()))
                            .collect(Collectors.joining(",\n")) +
                    "\n\nDựa vào danh sách trên, hãy gợi ý các **món ăn kèm phù hợp** với từng món chính, có thể dùng để chế biến cùng hoặc ăn kèm trong cùng bữa ăn (BREAKFAST, LUNCH, DINNER hoặc SNACK).\n\n" +
                    "Yêu cầu:\n" +
                    "- Kết quả trả về là danh sách món ăn kèm dưới dạng JSON hợp lệ, theo đúng định dạng mẫu:\n\n" +
                    "    {\n" +
                    "        \"name\": \"Rau cải luộc\",\n" +
                    "        \"calories\": 25.0,\n" +
                    "        \"protein\": 1.0,\n" +
                    "        \"fat\": 0.1,\n" +
                    "        \"carbs\": 5.0,\n" +
                    "        \"fiber\": 2.0\n" +
                    "    }\n\n" +
                    "- Không trùng với món chính đã cho.\n" +
                    "- Mỗi món phải có đủ thông tin: name, calories, protein, fat, carbs, fiber.\n" +
                    "- Tổng số món ăn kèm nên từ 4 đến 8 món.\n" +
                    "- Trả về kết quả JSON thuần, không giải thích, không có chú thích hay dấu ```.";


            log.info(promptText);
            // ✅ Tạo GeminiRequest đúng định dạng
            GeminiRequest request = new GeminiRequest(
                    List.of(new GeminiRequest.Content("user", List.of(new GeminiRequest.Part(promptText))))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiRequest> requestHttpEntity = new HttpEntity<>(request, headers);

            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestHttpEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String foodJson = jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
            if (foodJson.startsWith("```")) {
                int firstNewLine = foodJson.indexOf("\n");
                int lastBacktick = foodJson.lastIndexOf("```");
                if (firstNewLine != -1 && lastBacktick != -1 && lastBacktick > firstNewLine) {
                    foodJson = foodJson.substring(firstNewLine + 1, lastBacktick).trim();
                }
            }
            return objectMapper.readValue(foodJson, new TypeReference<List<Food>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi Gemini API: " + e.getMessage(), e);
        }
    }

    public Food generateNewFood(String foodName) {
        try {
            String promptText = "Hãy cung cấp thông tin dinh dưỡng cho món ăn sau dưới dạng JSON hợp lệ.\n" +
                    "\n" +
                    "Tên món ăn: " + foodName + "\n" +
                    "\n" +
                    "Yêu cầu:\n" +
                    "- Kết quả trả về phải là một object JSON duy nhất, theo đúng định dạng mẫu sau:\n" +
                    "{\n" +
                    "  \"name\": \"Rau cải luộc\",\n" +
                    "  \"calories\": 25.0,\n" +
                    "  \"protein\": 1.0,\n" +
                    "  \"fat\": 0.1,\n" +
                    "  \"carbs\": 5.0,\n" +
                    "  \"fiber\": 2.0,\n" +
                    "  \"servingSize\": \"100g\"\n" +
                    "}\n" +
                    "- Các đơn vị:\n" +
                    "  - calories: kcal\n" +
                    "  - protein, fat, carbs, fiber: tính theo gram (g)\n" +
                    "  - servingSize có thể là 100g hoặc 100ml với thức uống, không sử dụng đơn vị 1 bowl cho các món nước, không sử dụng đơn vị cái cho các món bánh\n" +
                    "- Không kèm theo giải thích, không chú thích, không có dấu ```.\n" +
                    "- Nếu không có dữ liệu chính xác, hãy ước lượng theo mức thông thường của món ăn đó.\n" +
                    "- Nếu tên món ăn không hợp lệ: ví dụ như tên người, tên địa chỉ hay tên đồ vật khác, hãy trả về 1 object rỗng \n" ;

            log.info(promptText);

            GeminiRequest request = new GeminiRequest(
                    List.of(new GeminiRequest.Content("user", List.of(new GeminiRequest.Part(promptText))))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiRequest> requestHttpEntity = new HttpEntity<>(request, headers);

            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestHttpEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String foodJson = jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Gỡ markdown nếu có
            if (foodJson.startsWith("```")) {
                int firstNewLine = foodJson.indexOf("\n");
                int lastBacktick = foodJson.lastIndexOf("```");
                if (firstNewLine != -1 && lastBacktick != -1 && lastBacktick > firstNewLine) {
                    foodJson = foodJson.substring(firstNewLine + 1, lastBacktick).trim();
                }
            }

            // Chuyển trực tiếp sang 1 object Food
            return objectMapper.readValue(foodJson, Food.class);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi Gemini API: " + e.getMessage(), e);
        }
    }

}
