package com.dev.CaloApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginGoogleResponse {
    private Long id;
    private String email;
    private String name;
    private String picture;
}
