package com.AuraHealth.api.auraentities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    @Column(length = 100)
    private String specialty;

    @Column(name = "clinic_name", length = 200)
    private String clinicName;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time")
    private LocalTime appointmentTime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = Boolean.FALSE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPersist() {
        this.createdAt = LocalDateTime.now();
    }
}
