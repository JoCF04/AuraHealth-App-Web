package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auraentities.Medication;
import com.AuraHealth.api.auraentities.User;
import com.AuraHealth.api.aurarepositories.MedicationRepository;
import com.AuraHealth.api.aurarepositories.UserRepository;
import com.AuraHealth.api.auradtos.MedicationRequestDTO;
import com.AuraHealth.api.auradtos.MedicationResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final UserRepository       userRepository;

    public MedicationService(MedicationRepository medicationRepository, UserRepository userRepository) {
        this.medicationRepository = medicationRepository;
        this.userRepository       = userRepository;
    }

    // ── HU08 — Crear medicamento ──────────────────────────────────────────────

    @Transactional
    public MedicationResponseDTO crear(Long userId, MedicationRequestDTO dto) {
        User user = requireUser(userId);
        assertNoDuplicate(userId, dto.getName(), null);
        Medication m = new Medication();
        applyDto(dto, m, user);
        return toDto(medicationRepository.save(m));
    }

    // ── HU09 — Listar / Detalle ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MedicationResponseDTO> listar(Long userId) {
        requireUserExists(userId);
        return medicationRepository.findByUserIdOrderByNameAsc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicationResponseDTO obtenerPorId(Long userId, Long id) {
        return toDto(requireMedication(userId, id));
    }

    // ── HU08 — Actualizar medicamento ─────────────────────────────────────────

    @Transactional
    public MedicationResponseDTO actualizar(Long userId, Long id, MedicationRequestDTO dto) {
        User user = requireUser(userId);
        Medication m = requireMedication(userId, id);
        assertNoDuplicate(userId, dto.getName(), id);
        applyDto(dto, m, user);
        return toDto(medicationRepository.save(m));
    }

    // ── HU10 — Eliminar ──────────────────────────────────────────────────────

    @Transactional
    public void eliminar(Long userId, Long id) {
        medicationRepository.delete(requireMedication(userId, id));
    }

    // ── HU09 — Toggle diario ─────────────────────────────────────────────────

    @Transactional
    public MedicationResponseDTO toggleCompletadoHoy(Long userId, Long id) {
        Medication m = requireMedication(userId, id);
        m.setIsCompletedToday(!Boolean.TRUE.equals(m.getIsCompletedToday()));
        return toDto(medicationRepository.save(m));
    }

    // ── HU30 — Toggle privacidad con Partner ─────────────────────────────────

    @Transactional
    public MedicationResponseDTO togglePrivacidad(Long userId, Long id) {
        Medication m = requireMedication(userId, id);
        m.setIsSharedWithPartner(!Boolean.TRUE.equals(m.getIsSharedWithPartner()));
        return toDto(medicationRepository.save(m));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertNoDuplicate(Long userId, String name, Long excludeId) {
        if (name == null || name.isBlank()) return;
        String normalised = name.toLowerCase().strip();
        boolean conflict = medicationRepository.findByUserIdOrderByNameAsc(userId).stream()
            .filter(m -> excludeId == null || !m.getId().equals(excludeId))
            .anyMatch(m -> m.getName().toLowerCase().strip().equals(normalised));
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya tienes '" + name.strip() + "' en tu lista de medicamentos activos. "
                + "Verifica la dosis antes de agregar un duplicado.");
        }
    }

    private void applyDto(MedicationRequestDTO dto, Medication m, User user) {
        m.setUser(user);
        m.setName(dto.getName().strip());
        m.setDosage(dto.getDosage());
        m.setFrequency(dto.getFrequency());
        m.setStartDate(parseDate(dto.getStartDate()));
        m.setEndDate(parseDate(dto.getEndDate()));
        m.setIsSharedWithPartner(dto.getIsSharedWithPartner() != null ? dto.getIsSharedWithPartner() : Boolean.FALSE);
        if (m.getIsCompletedToday() == null) m.setIsCompletedToday(Boolean.FALSE);
    }

    private LocalDate parseDate(String raw) {
        return (raw != null && !raw.isBlank()) ? LocalDate.parse(raw) : null;
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + userId));
    }

    private void requireUserExists(Long userId) {
        if (!userRepository.existsById(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + userId);
    }

    private Medication requireMedication(Long userId, Long id) {
        return medicationRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Medicamento con id " + id + " no encontrado para el usuario " + userId));
    }

    MedicationResponseDTO toDto(Medication m) {
        MedicationResponseDTO dto = new MedicationResponseDTO();
        dto.setId(m.getId());
        dto.setUserId(m.getUser().getId());
        dto.setName(m.getName());
        dto.setDosage(m.getDosage());
        dto.setFrequency(m.getFrequency());
        dto.setStartDate(m.getStartDate());
        dto.setEndDate(m.getEndDate());
        dto.setIsSharedWithPartner(m.getIsSharedWithPartner());
        dto.setIsCompletedToday(m.getIsCompletedToday());
        return dto;
    }
}