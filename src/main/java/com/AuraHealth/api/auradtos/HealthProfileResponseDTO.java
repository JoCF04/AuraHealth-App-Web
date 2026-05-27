package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileResponseDTO {

    private Long       userId;
    private String     bloodType;
    private String     bloodPressure;
    private BigDecimal glucoseLevel;
    private BigDecimal cholesterolLevel;
    private String     allergies;
    private BigDecimal weightKg;
    private BigDecimal heightCm;

    /** IMC calculado exclusivamente en el backend (HU05). */
    private BigDecimal bmi;

    /** Categoría OMS: "Bajo peso" / "Normal" / "Sobrepeso" / "Obesidad". */
    private String bmiCategory;

    /** true si algún signo vital supera el umbral clínico (HU07). */
    private Boolean vitalAlertFlag;

    /** Descripción de la alerta. null cuando vitalAlertFlag = false. */
    private String alertMessage;
}