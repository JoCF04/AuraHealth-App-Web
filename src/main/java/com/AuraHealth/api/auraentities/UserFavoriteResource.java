package com.AuraHealth.api.auraentities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_favorite_resources",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFavoriteResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private EducationalResource resource;

    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onPersist() {
        this.savedAt = LocalDateTime.now();
    }
}
