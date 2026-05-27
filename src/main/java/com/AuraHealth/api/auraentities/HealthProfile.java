package com.AuraHealth.api.auraentities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "health_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "blood_type", length = 10)
    private String bloodType;

    /** Formato: "sistólica/diastólica mmHg", ej: "120/80 mmHg". */
    @Column(name = "blood_pressure", length = 30)
    private String bloodPressure;

    /** Glucosa en ayunas (mg/dL). */
    @Column(name = "glucose_level", precision = 6, scale = 2)
    private BigDecimal glucoseLevel;

    /** Colesterol total (mg/dL). */
    @Column(name = "cholesterol_level", precision = 6, scale = 2)
    private BigDecimal cholesterolLevel;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 6, scale = 2)
    private BigDecimal heightCm;

    // ── Campos calculados por el Domain Layer — nunca vienen del cliente ──────

    /**
     * Índice de Masa Corporal: peso_kg / (altura_m)².
     * Calculado exclusivamente en el backend (HU05).
     */
    @Column(name = "bmi", precision = 5, scale = 2)
    private BigDecimal bmi;

    /**
     * Categoría OMS: "Bajo peso" | "Normal" | "Sobrepeso" | "Obesidad".
     */
    @Column(name = "bmi_category", length = 20)
    private String bmiCategory;

    /**
     * true cuando algún signo vital supera el umbral clínico (HU07 — Motor de Reglas Médicas).
     */
    @Column(name = "vital_alert_flag", nullable = false)
    private Boolean vitalAlertFlag = Boolean.FALSE;

    /**
     * Descripción legible de la alerta. null cuando vitalAlertFlag = false.
     */
    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;

    @PrePersist
    protected void onCreate() {
        if (vitalAlertFlag == null) vitalAlertFlag = Boolean.FALSE;
    }
}