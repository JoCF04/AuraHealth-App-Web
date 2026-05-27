package com.AuraHealth.api.auraentities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String dosage;

    private String frequency;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_shared_with_partner", nullable = false)
    private Boolean isSharedWithPartner = Boolean.FALSE;

    @Column(name = "is_completed_today", nullable = false)
    private Boolean isCompletedToday = Boolean.FALSE;

    @PrePersist
    protected void onCreate() {
        if (this.isSharedWithPartner == null) this.isSharedWithPartner = Boolean.FALSE;
        if (this.isCompletedToday == null) this.isCompletedToday = Boolean.FALSE;
    }
}