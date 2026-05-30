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

@RestController
@RequestMapping("/api/v1")
@Tag(name = "EP03 · Medication Management",
     description = "HU08 — Registro y actualización de medicamentos")
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
}
