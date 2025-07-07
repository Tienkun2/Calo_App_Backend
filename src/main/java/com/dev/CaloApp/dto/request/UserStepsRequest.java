package com.dev.CaloApp.dto.request;

import com.dev.CaloApp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserStepsRequest {
    private int steps = 0;
    private LocalDate date = LocalDate.now();
}
