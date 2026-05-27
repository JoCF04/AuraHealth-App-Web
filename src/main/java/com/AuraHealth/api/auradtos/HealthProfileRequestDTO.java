package com.AuraHealth.api.auradtos;

import lombok.*;
import java.math.BigDecimal;

/**
 * Todos los campos son opcionales: se actualiza solo lo que se envíe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileRequestDTO {

    private String bloodType;
    private String bloodPressure;
    private BigDecimal glucoseLevel;
    private BigDecimal cholesterolLevel;
    private String allergies;
    private BigDecimal weightKg;
    private BigDecimal heightCm;
}