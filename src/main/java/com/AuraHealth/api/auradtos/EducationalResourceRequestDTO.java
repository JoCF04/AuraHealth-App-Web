package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EducationalResourceRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    private String description;
    private String content;
    private String imageUrl;
    private String author;

    @NotNull(message = "El tipo de formato es obligatorio")
    private String formatType;  // Se parsea a ResourceFormat enum en el Service

    private String downloadUrl;
    private Boolean isPublished = Boolean.TRUE;
}