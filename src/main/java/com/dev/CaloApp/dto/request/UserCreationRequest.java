package com.dev.CaloApp.dto.request;

import com.dev.CaloApp.Enum.ActivityLevel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @NotBlank(message = "EMAIL_NOT_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_NOT_BLANK")
    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    private String password;
//    // Thêm trường activityLevel
//    @NotNull(message = "ACTIVITY_LEVEL_NOT_NULL")
//    private ActivityLevel activityLevel;
}