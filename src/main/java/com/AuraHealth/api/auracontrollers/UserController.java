package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.*;
import com.AuraHealth.api.auraservices.UserService;
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
@Tag(name = "EP01-EP02 · Users & Health Profiles",
        description = "HU01-HU07 — Registro, perfil, signos vitales, IMC e idioma")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registrarUsuario(dto));
    }

    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerUsuarioPorId(id));
    }

    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @PutMapping("/users/{userId}/health-profile")
    public ResponseEntity<HealthProfileResponseDTO> updateHealthProfile(@PathVariable Long userId, @RequestBody HealthProfileRequestDTO dto) {
        return ResponseEntity.ok(userService.actualizarPerfilDeSalud(userId, dto));
    }

    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @PatchMapping("/users/{userId}/vital-signs")
    public ResponseEntity<HealthProfileResponseDTO> updateVitalSigns(@PathVariable Long userId, @RequestBody VitalSignsRequestDTO dto) {
        return ResponseEntity.ok(userService.registrarSignosVitales(userId, dto));
    }

    // ── HU06 — Cambiar idioma preferido (Masiel) ──────────────────────────────

    @Operation(summary = "HU06 — Actualizar idioma preferido (es | en)",
            description = "Roles: USER · ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Idioma actualizado."),
            @ApiResponse(responseCode = "400", description = "Código de idioma inválido (solo: es, en)."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/users/{id}/language")
    public ResponseEntity<UserResponseDTO> updateLanguage(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Código ISO 639-1: es | en", example = "en", required = true)
            @RequestParam String lang) {
        return ResponseEntity.ok(userService.cambiarIdioma(id, lang));
    }
}