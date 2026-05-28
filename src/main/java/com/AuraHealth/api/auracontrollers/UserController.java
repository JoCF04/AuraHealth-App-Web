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
     description = "HU01-HU04 — Registro y perfil de usuario")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ── HU01 — Registrar usuario (Jose) ──────────────────────────────────────

    @Operation(summary = "HU01 — Registrar nuevo usuario", description = "Acceso: PÚBLICO")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado."),
        @ApiResponse(responseCode = "400", description = "Datos inválidos."),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado.")
    })
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registrarUsuario(dto));
    }

    // ── HU04 — Ver perfil (Lucia) ─────────────────────────────────────────────

    @Operation(summary = "HU04 — Obtener perfil completo del usuario",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil obtenido."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerUsuarioPorId(id));
    }
}
