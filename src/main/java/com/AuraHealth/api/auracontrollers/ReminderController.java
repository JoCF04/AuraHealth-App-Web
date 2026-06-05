package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auradtos.*;
import com.AuraHealth.api.auraservices.ReminderService;
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
@Tag(name = "EP04 · Reminders & Appointments",
     description = "HU11-HU14, HU24-HU25 — Recordatorios médicos y citas")
@PreAuthorize("hasRole('USER')")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // ══ RECORDATORIOS ═════════════════════════════════════════════════════════

    @Operation(summary = "HU12 — Crear recordatorio (fecha debe ser futura)",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Recordatorio creado."),
        @ApiResponse(responseCode = "400", description = "Fecha pasada, tipo inválido o datos faltantes."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/users/{userId}/reminders")
    public ResponseEntity<ReminderResponseDTO> createReminder(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ReminderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.crearRecordatorio(userId, dto));
    }

    @Operation(summary = "HU11/HU13 — Listar recordatorios (filtro opcional por tipo)",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/reminders")
    public ResponseEntity<List<ReminderResponseDTO>> listReminders(
            @PathVariable Long userId,
            @Parameter(description = "medical | medicine | exam | vaccine")
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(reminderService.listarRecordatorios(userId, type));
    }

    @Operation(summary = "HU11 — Listar recordatorios pendientes (isDone=false, fecha ≥ hoy)",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/reminders/pending")
    public ResponseEntity<List<ReminderResponseDTO>> listPendingReminders(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.listarPendientes(userId));
    }

    @Operation(summary = "HU14 — Listar recordatorios vencidos (isDone=false, fecha < hoy)",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/reminders/overdue")
    public ResponseEntity<List<ReminderResponseDTO>> listOverdueReminders(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.listarVencidos(userId));
    }

    @Operation(summary = "HU11 — Obtener detalle de un recordatorio",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/reminders/{id}")
    public ResponseEntity<ReminderResponseDTO> getReminder(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(reminderService.obtenerRecordatorioPorId(userId, id));
    }

    @Operation(summary = "HU12 — Actualizar recordatorio",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/users/{userId}/reminders/{id}")
    public ResponseEntity<ReminderResponseDTO> updateReminder(
            @PathVariable Long userId, @PathVariable Long id,
            @Valid @RequestBody ReminderRequestDTO dto) {
        return ResponseEntity.ok(reminderService.actualizarRecordatorio(userId, id, dto));
    }

    @Operation(summary = "HU14 — Marcar recordatorio como completado",
               description = "Roles: USER · ADMIN — 409 si ya estaba completado.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/users/{userId}/reminders/{id}/complete")
    public ResponseEntity<ReminderResponseDTO> completeReminder(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(reminderService.marcarComoCompletado(userId, id));
    }

    @Operation(summary = "HU12 — Eliminar recordatorio",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/users/{userId}/reminders/{id}")
    public ResponseEntity<Void> deleteReminder(
            @PathVariable Long userId, @PathVariable Long id) {
        reminderService.eliminarRecordatorio(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ══ CITAS MÉDICAS ═════════════════════════════════════════════════════════

    @Operation(summary = "HU24 — Agendar cita médica (fecha futura obligatoria)",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cita agendada."),
        @ApiResponse(responseCode = "400", description = "Fecha pasada o datos faltantes."),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @ApiResponse(responseCode = "403", description = "Sin permisos."),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/users/{userId}/appointments")
    public ResponseEntity<AppointmentResponseDTO> scheduleAppointment(
            @PathVariable Long userId,
            @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderService.agendarCita(userId, dto));
    }

    @Operation(summary = "HU25 — Listar todas las citas del usuario",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> listAppointments(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.listarCitas(userId));
    }

    @Operation(summary = "HU25 — Próximas citas (fecha ≥ hoy)",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/appointments/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> listUpcomingAppointments(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.listarProximasCitas(userId));
    }

    @Operation(summary = "HU25 — Detalle de una cita",
               description = "Roles: USER · DOCTOR · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','DOCTOR','ADMIN')")
    @GetMapping("/users/{userId}/appointments/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(
            @PathVariable Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(reminderService.obtenerCitaPorId(userId, id));
    }

    @Operation(summary = "HU24 — Actualizar cita médica",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/users/{userId}/appointments/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable Long userId, @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(reminderService.actualizarCita(userId, id, dto));
    }

    @Operation(summary = "HU24 — Cancelar cita médica",
               description = "Roles: USER · ADMIN",
               security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/users/{userId}/appointments/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long userId, @PathVariable Long id) {
        reminderService.cancelarCita(userId, id);
        return ResponseEntity.noContent().build();
    }
}
