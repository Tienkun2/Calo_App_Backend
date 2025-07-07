package com.dev.CaloApp.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IntrospectResponse {
     boolean valid;
}
