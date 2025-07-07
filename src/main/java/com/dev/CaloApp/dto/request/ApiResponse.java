package com.dev.CaloApp.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code = 1000;
    private String message;
    private T result;
}
