package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequestDTO {

    private String doctorName;

    private String specialty;

    private String clinicName;

    @NotNull(message = "La fecha de la cita es obligatoria")
    private String appointmentDate;  // Formato: YYYY-MM-DD

    private String appointmentTime;  // Formato: HH:mm (opcional)

    private String notes;

    private Boolean isConfirmed = Boolean.FALSE;
}