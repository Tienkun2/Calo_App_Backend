package com.dev.CaloApp.controller;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.GeminiRequest;
import com.dev.CaloApp.service.GeminiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> generateText(@RequestBody GeminiRequest request) {
        ApiResponse<String> response = geminiService.generateContent(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
