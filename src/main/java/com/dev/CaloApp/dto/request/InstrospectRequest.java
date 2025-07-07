package com.dev.CaloApp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InstrospectRequest {
    private String token;
}
