package com.dev.CaloApp.dto.response;

import com.dev.CaloApp.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserStepsResponse {
    private User user;
    private LocalDate date;
    private int steps;
    private float calories;
}
