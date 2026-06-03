package com.AuraHealth.api.auradtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ActivityUpdateRequestDTO {
    private Integer stepsCount;
    private BigDecimal waterLiters;
    private BigDecimal sleepHours;
    private Integer caloriesKcal;
}