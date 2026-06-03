package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auraentities.ActivityLog;
import com.AuraHealth.api.auraentities.User;
import com.AuraHealth.api.aurarepositories.ActivityLogRepository;
import com.AuraHealth.api.aurarepositories.UserRepository;
import com.AuraHealth.api.auradtos.ActivityLogResponseDTO;
import com.AuraHealth.api.auradtos.ActivityUpdateRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class UserActivityService {

    private final ActivityLogRepository activityRepository;
    private final UserRepository        userRepository;

    public UserActivityService(ActivityLogRepository activityRepository,
                               UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository     = userRepository;
    }

    // ── HU16/17/18/19 — Registrar o actualizar actividad de hoy ──────────────

    @Transactional
    public ActivityLogResponseDTO actualizarActividad(Long userId, ActivityUpdateRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + userId));

        // Upsert: busca el log de hoy o crea uno nuevo
        ActivityLog log = activityRepository.findByUserIdAndLogDate(userId, LocalDate.now())
            .orElseGet(() -> {
                ActivityLog newLog = new ActivityLog();
                newLog.setUser(user);
                newLog.setLogDate(LocalDate.now());
                return newLog;
            });

        if (dto.getStepsCount()   != null) log.setStepsCount(dto.getStepsCount());
        if (dto.getWaterLiters()  != null) log.setWaterLiters(dto.getWaterLiters());
        if (dto.getSleepHours()   != null) log.setSleepHours(dto.getSleepHours());
        if (dto.getCaloriesKcal() != null) log.setCaloriesKcal(dto.getCaloriesKcal());

        return toDto(activityRepository.save(log));
    }

    // ── HU15/20 — Obtener resumen de actividad de hoy ────────────────────────

    @Transactional(readOnly = true)
    public ActivityLogResponseDTO obtenerActividadHoy(Long userId) {
        return activityRepository.findByUserIdAndLogDate(userId, LocalDate.now())
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No hay actividad registrada para hoy"));
    }

    private ActivityLogResponseDTO toDto(ActivityLog log) {
        return new ActivityLogResponseDTO(
            log.getId(), log.getUser().getId(), log.getLogDate(),
            log.getStepsCount(), log.getWaterLiters(), log.getSleepHours(), log.getCaloriesKcal()
        );
    }
}