package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentRequestDTO {

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String doctorName;

    private String specialty;
    private String clinicName;

    @NotBlank(message = "La fecha de la cita es obligatoria")
    private String appointmentDate;

    private String appointmentTime;
    private String notes;
    private Boolean isConfirmed;
}
