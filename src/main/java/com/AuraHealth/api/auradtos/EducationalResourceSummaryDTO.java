package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationalResourceSummaryDTO {
    private Long id;
    private String title;
    private String category;
    private String description;
    private String imageUrl;
    private String author;
    private String formatType;
}