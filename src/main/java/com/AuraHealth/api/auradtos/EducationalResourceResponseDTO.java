package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationalResourceResponseDTO {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String content;
    private String imageUrl;
    private String author;
    private String formatType;
    private String downloadUrl;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}