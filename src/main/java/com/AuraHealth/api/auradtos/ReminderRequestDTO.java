package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReminderRequestDTO {

    @NotBlank(message = "El título del recordatorio es obligatorio")
    private String title;

    @NotBlank(message = "El tipo de recordatorio es obligatorio")
    @Pattern(
        regexp  = "medical|medicine|exam|vaccine",
        message = "Tipo inválido. Valores aceptados: medical, medicine, exam, vaccine"
    )
    private String reminderType;

    @NotNull(message = "La fecha programada es obligatoria")
    private String scheduledDate;  // Formato ISO: YYYY-MM-DD

    private String scheduledTime;  // Formato: HH:mm (opcional)
}