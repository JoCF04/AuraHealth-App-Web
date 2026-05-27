package com.AuraHealth.api.auradtos;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Todos los campos son opcionales: PATCH aplica únicamente los valores enviados.
 * El Motor de Reglas Médicas valida contra umbrales OMS / ADA / AHA.
 */
@Data
public class VitalSignsRequestDTO {

    /** Glucosa en ayunas (mg/dL). Umbral de alerta: ≥ 126 mg/dL (ADA). */
    private BigDecimal glucoseLevel;

    /** Formato: "sistólica/diastólica mmHg". Umbral: ≥ 140/90 mmHg (AHA). */
    private String bloodPressure;

    /** Colesterol total (mg/dL). Umbral: ≥ 240 mg/dL (NCEP). */
    private BigDecimal cholesterolLevel;

    /** Alergias conocidas (texto libre). Sin umbral de alerta. */
    private String allergies;
}