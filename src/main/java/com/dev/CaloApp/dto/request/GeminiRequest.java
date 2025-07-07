package com.dev.CaloApp.dto.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiRequest {
    private List<Content> contents;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String role; // "user" hoặc "model"
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
}
