package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicationRequestDTO {

    @NotBlank(message = "El nombre del medicamento es obligatorio")
    private String name;

    private String dosage;    // Ej: "500mg"

    private String frequency; // Ej: "Cada 8 horas"

    private String startDate; // Formato ISO: YYYY-MM-DD

    private String endDate;   // Opcional

    /** HU30 — Control de privacidad con el Partner. */
    private Boolean isSharedWithPartner = Boolean.FALSE;
}