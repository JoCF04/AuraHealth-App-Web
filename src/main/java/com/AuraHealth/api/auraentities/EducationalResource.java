package com.AuraHealth.api.auraentities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "educational_resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationalResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    private String author;

    @Enumerated(EnumType.STRING)
    @Column(name = "format_type", length = 50)
    private ResourceFormat formatType;

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "is_published")
    private Boolean isPublished = Boolean.TRUE;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFavoriteResource> favorites;

    @PrePersist
    protected void onPersist() {
        this.createdAt = LocalDateTime.now();
        if (this.publishedAt == null && Boolean.TRUE.equals(this.isPublished)) {
            this.publishedAt = LocalDateTime.now();
        }
    }
}