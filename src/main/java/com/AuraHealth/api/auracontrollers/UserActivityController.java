package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.ActivityLogResponseDTO;
import com.AuraHealth.api.auradtos.ActivityUpdateRequestDTO;
import com.AuraHealth.api.auraservices.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/activity")
@Tag(name = "EP05 · Wellness Monitoring",
     description = "Seguimiento diario de métricas de actividad (Pasos, Agua, Sueño).")
@PreAuthorize("hasRole('USER')")
public class UserActivityController {

    private final UserActivityService service;

    public UserActivityController(UserActivityService service) {
        this.service = service;
    }

    @Operation(summary = "HU16/17/18/19 (EP05) — Registrar o actualizar métricas de hoy",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping
    public ResponseEntity<ActivityLogResponseDTO> updateActivity(
            @PathVariable Long userId,
            @RequestBody ActivityUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarActividad(userId, dto));
    }

    @Operation(summary = "HU15/20 (EP05) — Obtener resumen de actividad de hoy",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/today")
    public ResponseEntity<ActivityLogResponseDTO> getToday(@PathVariable Long userId) {
        return ResponseEntity.ok(service.obtenerActividadHoy(userId));
    }
}
