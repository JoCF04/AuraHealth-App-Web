package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isSharedWithPartner;

    /** Estado del checkbox diario: true = tomado hoy. */
    private Boolean isCompletedToday;
}