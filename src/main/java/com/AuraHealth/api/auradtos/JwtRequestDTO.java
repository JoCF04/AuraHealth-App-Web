package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
