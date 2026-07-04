package com.AuraHealth.api.auradtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentResponseDTO {

    private Long          id;
    private Long          userId;
    private String        doctorName;
    private String        specialty;
    private String        clinicName;
    private LocalDate     appointmentDate;
    private LocalTime     appointmentTime;
    private String        notes;
    private Boolean       isConfirmed;
    private LocalDateTime createdAt;
}
