package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.OpenFdaInfoDTO;
import com.AuraHealth.api.auradtos.OpenFdaWarningsDTO;
import com.AuraHealth.api.auraservices.OpenFdaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medications")
@Tag(name = "EP09 · OpenFDA — Información de Medicamentos",
     description = "HU37 — Indicaciones/dosis · HU40 — Advertencias (fuente: openFDA, sin API key)")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class OpenFdaController {

    private final OpenFdaService openFdaService;

    public OpenFdaController(OpenFdaService openFdaService) {
        this.openFdaService = openFdaService;
    }

    // ── HU37 — Información del medicamento ───────────────────────────────────

    @Operation(
        summary = "HU37 — Información del medicamento (openFDA)",
        description = "Devuelve indicaciones, dosis y propósito desde openFDA. "
            + "Si el medicamento no existe en la base de datos de EE.UU., "
            + "responde 200 con encontrado=false y listas vacías.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Info obtenida. Revisar campo 'encontrado'."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos.")
    })
    @GetMapping("/{nombre}/openfda-info")
    public ResponseEntity<OpenFdaInfoDTO> getInfo(
            @Parameter(description = "Nombre genérico o de marca (ej: ibuprofen, Advil)",
                       example = "ibuprofen", required = true)
            @PathVariable String nombre) {
        return ResponseEntity.ok(openFdaService.obtenerInfo(nombre));
    }

    // ── HU40 — Advertencias del medicamento ──────────────────────────────────

    @Operation(
        summary = "HU40 — Advertencias del medicamento (openFDA)",
        description = "Devuelve advertencias, contraindicaciones y cuándo consultar al médico. "
            + "Si el medicamento no existe en la base de datos de EE.UU., "
            + "responde 200 con encontrado=false y listas vacías.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Advertencias obtenidas. Revisar campo 'encontrado'."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos.")
    })
    @GetMapping("/{nombre}/openfda-warnings")
    public ResponseEntity<OpenFdaWarningsDTO> getWarnings(
            @Parameter(description = "Nombre genérico o de marca (ej: ibuprofen, Advil)",
                       example = "ibuprofen", required = true)
            @PathVariable String nombre) {
        return ResponseEntity.ok(openFdaService.obtenerWarnings(nombre));
    }
}
