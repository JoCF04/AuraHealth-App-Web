package com.AuraHealth.api.auradtos;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long   id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDate     birthDate;
    private String        gender;
    private Boolean       isEmailVerified;
    private String        preferredLanguage;
    private LocalDateTime createdAt;

    private HealthProfileResponseDTO healthProfile;
}