package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auraentities.Appointment;
import com.AuraHealth.api.auraentities.Reminder;
import com.AuraHealth.api.auraentities.User;
import com.AuraHealth.api.aurarepositories.AppointmentRepository;
import com.AuraHealth.api.aurarepositories.ReminderRepository;
import com.AuraHealth.api.aurarepositories.UserRepository;
import com.AuraHealth.api.auradtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final ReminderRepository    reminderRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository        userRepository;

    public ReminderService(ReminderRepository    reminderRepository,
                           AppointmentRepository appointmentRepository,
                           UserRepository        userRepository) {
        this.reminderRepository    = reminderRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository        = userRepository;
    }

    // ── HU12 — Crear recordatorio ─────────────────────────────────────────────

    @Transactional
    public ReminderResponseDTO crearRecordatorio(Long userId, ReminderRequestDTO dto) {
        User user = requireUser(userId);
        LocalDate scheduled = parseAndAssertFutureDate(dto.getScheduledDate());

        Reminder r = new Reminder();
        r.setUser(user);
        r.setTitle(dto.getTitle());
        r.setReminderType(dto.getReminderType());
        r.setScheduledDate(scheduled);
        r.setScheduledTime(parseTime(dto.getScheduledTime()));
        r.setIsDone(Boolean.FALSE);

        return toReminderDto(reminderRepository.save(r));
    }

    // ── HU11 / HU13 — Listar / Filtrar ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReminderResponseDTO> listarRecordatorios(Long userId, String type) {
        requireUserExists(userId);
        List<Reminder> lista = (type != null && !type.isBlank())
            ? reminderRepository.findByUserIdAndReminderTypeIgnoreCaseOrderByScheduledDateAsc(userId, type)
            : reminderRepository.findByUserIdOrderByScheduledDateAsc(userId);
        return lista.stream().map(this::toReminderDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReminderResponseDTO obtenerRecordatorioPorId(Long userId, Long id) {
        return toReminderDto(requireReminder(userId, id));
    }

    @Transactional(readOnly = true)
    public List<ReminderResponseDTO> listarPendientes(Long userId) {
        requireUserExists(userId);
        return reminderRepository
            .findByUserIdAndIsDoneFalseAndScheduledDateGreaterThanEqualOrderByScheduledDateAsc(
                userId, LocalDate.now())
            .stream().map(this::toReminderDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReminderResponseDTO> listarVencidos(Long userId) {
        requireUserExists(userId);
        return reminderRepository
            .findByUserIdAndIsDoneFalseAndScheduledDateBeforeOrderByScheduledDateDesc(
                userId, LocalDate.now())
            .stream().map(this::toReminderDto).collect(Collectors.toList());
    }

    // ── HU12 — Actualizar / Eliminar ─────────────────────────────────────────

    @Transactional
    public ReminderResponseDTO actualizarRecordatorio(Long userId, Long id, ReminderRequestDTO dto) {
        User user = requireUser(userId);
        Reminder r = requireReminder(userId, id);
        LocalDate scheduled = parseAndAssertFutureDate(dto.getScheduledDate());
        r.setUser(user);
        r.setTitle(dto.getTitle());
        r.setReminderType(dto.getReminderType());
        r.setScheduledDate(scheduled);
        r.setScheduledTime(parseTime(dto.getScheduledTime()));
        return toReminderDto(reminderRepository.save(r));
    }

    // ── HU14 — Completar recordatorio ────────────────────────────────────────

    @Transactional
    public ReminderResponseDTO marcarComoCompletado(Long userId, Long id) {
        Reminder r = requireReminder(userId, id);
        if (Boolean.TRUE.equals(r.getIsDone())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El recordatorio con id " + id + " ya estaba marcado como completado");
        }
        r.setIsDone(Boolean.TRUE);
        return toReminderDto(reminderRepository.save(r));
    }

    @Transactional
    public void eliminarRecordatorio(Long userId, Long id) {
        reminderRepository.delete(requireReminder(userId, id));
    }

    // ── HU24 / HU25 — Citas médicas ──────────────────────────────────────────

    @Transactional
    public AppointmentResponseDTO agendarCita(Long userId, AppointmentRequestDTO dto) {
        User user = requireUser(userId);
        LocalDate date = parseAndAssertFutureDate(dto.getAppointmentDate());

        Appointment cita = new Appointment();
        cita.setUser(user);
        cita.setDoctorName(dto.getDoctorName());
        cita.setSpecialty(dto.getSpecialty());
        cita.setClinicName(dto.getClinicName());
        cita.setAppointmentDate(date);
        cita.setAppointmentTime(parseTime(dto.getAppointmentTime()));
        cita.setNotes(dto.getNotes());
        cita.setIsConfirmed(dto.getIsConfirmed() != null ? dto.getIsConfirmed() : Boolean.FALSE);

        return toAppointmentDto(appointmentRepository.save(cita));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listarCitas(Long userId) {
        requireUserExists(userId);
        return appointmentRepository.findByUserIdOrderByAppointmentDateAsc(userId)
            .stream().map(this::toAppointmentDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO obtenerCitaPorId(Long userId, Long id) {
        return toAppointmentDto(requireAppointment(userId, id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listarProximasCitas(Long userId) {
        requireUserExists(userId);
        return appointmentRepository
            .findByUserIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
                userId, LocalDate.now())
            .stream().map(this::toAppointmentDto).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponseDTO actualizarCita(Long userId, Long id, AppointmentRequestDTO dto) {
        User user = requireUser(userId);
        Appointment cita = requireAppointment(userId, id);
        LocalDate date = parseAndAssertFutureDate(dto.getAppointmentDate());
        cita.setUser(user);
        cita.setDoctorName(dto.getDoctorName());
        cita.setSpecialty(dto.getSpecialty());
        cita.setClinicName(dto.getClinicName());
        cita.setAppointmentDate(date);
        cita.setAppointmentTime(parseTime(dto.getAppointmentTime()));
        cita.setNotes(dto.getNotes());
        cita.setIsConfirmed(dto.getIsConfirmed() != null ? dto.getIsConfirmed() : cita.getIsConfirmed());
        return toAppointmentDto(appointmentRepository.save(cita));
    }

    @Transactional
    public void cancelarCita(Long userId, Long id) {
        appointmentRepository.delete(requireAppointment(userId, id));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private LocalDate parseAndAssertFutureDate(String raw) {
        if (raw == null || raw.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha programada es obligatoria");
        LocalDate date;
        try { date = LocalDate.parse(raw); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Formato de fecha inválido: '" + raw + "'. Use ISO-8601: 'YYYY-MM-DD'");
        }
        return date;
    }

    private LocalTime parseTime(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return LocalTime.parse(raw); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Formato de hora inválido: '" + raw + "'. Use 'HH:mm'");
        }
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + userId));
    }

    private void requireUserExists(Long userId) {
        if (!userRepository.existsById(userId))
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + userId);
    }

    private Reminder requireReminder(Long userId, Long id) {
        return reminderRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Recordatorio con id " + id + " no encontrado para el usuario " + userId));
    }

    private Appointment requireAppointment(Long userId, Long id) {
        return appointmentRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cita con id " + id + " no encontrada para el usuario " + userId));
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private ReminderResponseDTO toReminderDto(Reminder r) {
        ReminderResponseDTO dto = new ReminderResponseDTO();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setTitle(r.getTitle());
        dto.setReminderType(r.getReminderType());
        dto.setScheduledDate(r.getScheduledDate());
        dto.setScheduledTime(r.getScheduledTime());
        dto.setIsDone(r.getIsDone());
        return dto;
    }

    private AppointmentResponseDTO toAppointmentDto(Appointment a) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(a.getId());
        dto.setUserId(a.getUser().getId());
        dto.setDoctorName(a.getDoctorName());
        dto.setSpecialty(a.getSpecialty());
        dto.setClinicName(a.getClinicName());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setNotes(a.getNotes());
        dto.setIsConfirmed(a.getIsConfirmed());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}