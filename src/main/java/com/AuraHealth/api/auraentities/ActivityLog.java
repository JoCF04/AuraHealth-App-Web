package com.AuraHealth.api.auraentities;

import com.AuraHealth.api.auraentities.User;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "activity_logs")
@Data
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "steps_count")
    private Integer stepsCount = 0;

    @Column(name = "water_liters", precision = 4, scale = 2)
    private BigDecimal waterLiters = BigDecimal.ZERO;

    @Column(name = "sleep_hours", precision = 4, scale = 2)
    private BigDecimal sleepHours = BigDecimal.ZERO;

    @Column(name = "calories_kcal")
    private Integer caloriesKcal = 0;

}