package com.AuraHealth.api.auradtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReminderResponseDTO {

    private Long   id;
    private Long   userId;
    private String title;
    private String reminderType;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Boolean   isDone;
}