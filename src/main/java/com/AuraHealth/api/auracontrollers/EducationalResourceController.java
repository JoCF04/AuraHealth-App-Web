package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.*;
import com.AuraHealth.api.auraservices.EducationalResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@Tag(name = "EP06-EP09 · Educational Library",
     description = "Biblioteca de salud preventiva y recomendaciones personalizadas.")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class EducationalResourceController {

    private final EducationalResourceService service;

    public EducationalResourceController(EducationalResourceService service) {
        this.service = service;
    }

    @Operation(summary = "HU21/HU22 (EP06) — Listar recomendaciones de salud",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<EducationalResourceSummaryDTO>> getAll() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "HU26 (EP09) — Buscar artículos por palabra clave",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<EducationalResourceSummaryDTO>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(service.buscar(keyword));
    }

    @Operation(summary = "HU04 (EP05) — Obtener consejos rápidos para el banner diario",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/daily-tips")
    public ResponseEntity<List<DailyTipResponseDTO>> getDailyTips() {
        return ResponseEntity.ok(service.obtenerTipsDelDia());
    }

    @Operation(summary = "HU23 (EP06) — Filtrar artículos por categoría (Nutrición, Ejercicio, etc.)",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EducationalResourceSummaryDTO>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(service.filtrarPorCategoria(category));
    }
}