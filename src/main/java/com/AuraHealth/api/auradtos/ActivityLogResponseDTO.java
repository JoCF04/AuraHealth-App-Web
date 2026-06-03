package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ActivityLogResponseDTO {
    private Long id;
    private Long userId;
    private LocalDate logDate;
    private Integer stepsCount;
    private BigDecimal waterLiters;
    private BigDecimal sleepHours;
    private Integer caloriesKcal;
}