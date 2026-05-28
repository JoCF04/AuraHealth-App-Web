package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@Tag(name = "EP06 · Educational Library",
     description = "HU21/HU22 — Biblioteca de recursos de salud preventiva.")
public class EducationalResourceController {

    private final EducationalResourceService service;

    public EducationalResourceController(EducationalResourceService service) {
        this.service = service;
    }

    // ── HU21/HU22 — Listar todos los recursos publicados ─────────────────────

    @Operation(summary = "HU21/HU22 — Listar recomendaciones y biblioteca de salud",
               description = "Roles: USER · DOCTOR · ADMIN — Solo devuelve recursos con isPublished=true.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista de recursos publicados.")
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<EducationalResourceSummaryDTO>> getAll() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // ── HU04 (soporte) — Consejos del día ─────────────────────────────────────

    @Operation(summary = "HU04 (soporte) — Obtener hasta 3 consejos del día (aleatorios)",
               description = "Roles: USER · DOCTOR · ADMIN — Usados por el banner de tips en el dashboard.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista de hasta 3 daily tips activos.")
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/daily-tips")
    public ResponseEntity<List<DailyTipResponseDTO>> getDailyTips() {
        return ResponseEntity.ok(service.obtenerTipsDelDia());
    }
}
