package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private Long   userId;
    private String email;
    private String role;
}
