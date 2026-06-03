package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.MedicationRequestDTO;
import com.AuraHealth.api.auradtos.MedicationResponseDTO;
import com.AuraHealth.api.auraservices.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "EP03 · Medication Management",
     description = "HU08-HU10 — Registro, seguimiento diario y eliminación de medicamentos")
@PreAuthorize("hasRole('USER')")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    // ── HU08 — Registrar nuevo medicamento ────────────────────────────────────

    @Operation(summary = "HU08 — Registrar nuevo medicamento",
               description = "Roles: USER · ADMIN — Valida duplicidad por nombre (case-insensitive).",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Medicamento registrado."),
        @ApiResponse(responseCode = "400", description = "Nombre obligatorio u otros datos inválidos."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
        @ApiResponse(responseCode = "409", description = "Medicamento duplicado — verifica la dosis.")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/users/{userId}/medications")
    public ResponseEntity<MedicationResponseDTO> createMedication(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody MedicationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.crear(userId, dto));
    }

    // ── HU09 — Listar ────────────────────────────────────────────────────────

    @Operation(summary = "HU09 — Listar medicamentos del usuario",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida (puede ser vacía)."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/medications")
    public ResponseEntity<List<MedicationResponseDTO>> listMedications(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(medicationService.listar(userId));
    }

    // ── HU09 — Detalle ───────────────────────────────────────────────────────

    @Operation(summary = "HU09 — Obtener detalle de un medicamento",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/medications/{id}")
    public ResponseEntity<MedicationResponseDTO> getMedication(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(medicationService.obtenerPorId(userId, id));
    }

    // ── HU08 — Actualizar medicamento existente ───────────────────────────────

    @Operation(summary = "HU08 — Actualizar medicamento",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medicamento actualizado."),
        @ApiResponse(responseCode = "404", description = "Medicamento o usuario no encontrado."),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado.")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/users/{userId}/medications/{id}")
    public ResponseEntity<MedicationResponseDTO> updateMedication(
            @PathVariable Long userId,
            @PathVariable Long id,
            @Valid @RequestBody MedicationRequestDTO dto) {
        return ResponseEntity.ok(medicationService.actualizar(userId, id, dto));
    }

    // ── HU10 — Eliminar ──────────────────────────────────────────────────────

    @Operation(summary = "HU10 — Eliminar medicamento",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Medicamento eliminado.")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/users/{userId}/medications/{id}")
    public ResponseEntity<Void> deleteMedication(
            @PathVariable Long userId, @PathVariable Long id) {
        medicationService.eliminar(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ── HU09 — Toggle diario ─────────────────────────────────────────────────

    @Operation(summary = "HU09 — Marcar/desmarcar toma de hoy (toggle)",
               description = "Roles: USER · ADMIN — Idempotente.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Estado actualizado.")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/users/{userId}/medications/{id}/toggle-today")
    public ResponseEntity<MedicationResponseDTO> toggleTodayCheckbox(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(medicationService.toggleCompletadoHoy(userId, id));
    }

    // ── HU30 — Toggle privacidad ──────────────────────────────────────────────

    @Operation(summary = "HU30 — Alternar visibilidad con Partner",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/users/{userId}/medications/{id}/toggle-privacy")
    public ResponseEntity<MedicationResponseDTO> togglePartnerVisibility(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(medicationService.togglePrivacidad(userId, id));
    }
}