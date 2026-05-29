package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auraentities.Role;
import com.AuraHealth.api.auraentities.HealthProfile;
import com.AuraHealth.api.auraentities.User;
import com.AuraHealth.api.aurarepositories.RoleRepository;
import com.AuraHealth.api.aurarepositories.HealthProfileRepository;
import com.AuraHealth.api.aurarepositories.UserRepository;
import com.AuraHealth.api.auradtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository          userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final RoleRepository          roleRepository;
    private final PasswordEncoder         passwordEncoder;

    public UserService(UserRepository userRepository,
                       HealthProfileRepository healthProfileRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository          = userRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.roleRepository          = roleRepository;
        this.passwordEncoder         = passwordEncoder;
    }

    // ── HU01 — Registrar usuario ──────────────────────────────────────────────

    @Transactional
    public UserResponseDTO registrarUsuario(UserRegistrationRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El correo '" + dto.getEmail() + "' ya está registrado");
        }

        String roleName = "ROLE_" + (dto.getRole() != null ? dto.getRole() : "USER");
        Role userRole = roleRepository.findByName(roleName)
            .orElseGet(() -> roleRepository.save(new Role(roleName)));

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().strip());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setGender(dto.getGender());
        user.setIsEmailVerified(true);
        user.setPreferredLanguage(
            dto.getPreferredLanguage() != null ? dto.getPreferredLanguage() : "es");
        user.setRoles(new HashSet<>(Set.of(userRole)));

        if (dto.getBirthDate() != null && !dto.getBirthDate().isBlank()) {
            user.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }

        return toUserDto(userRepository.save(user));
    }

    // ── HU04 — Ver perfil (Lucia) ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorId(Long id) {
        return toUserDto(requireUser(id));
    }

    // ── HU05 — Actualizar perfil de salud + IMC (Stefany) ────────────────────

    private static final java.math.BigDecimal BMI_UNDERWEIGHT    = new java.math.BigDecimal("18.5");
    private static final java.math.BigDecimal BMI_NORMAL_MAX     = new java.math.BigDecimal("24.9");
    private static final java.math.BigDecimal BMI_OVERWEIGHT_MAX = new java.math.BigDecimal("29.9");
    private static final java.math.BigDecimal MIN_WEIGHT_KG = new java.math.BigDecimal("1");
    private static final java.math.BigDecimal MAX_WEIGHT_KG = new java.math.BigDecimal("500");
    private static final java.math.BigDecimal MIN_HEIGHT_CM = new java.math.BigDecimal("30");
    private static final java.math.BigDecimal MAX_HEIGHT_CM = new java.math.BigDecimal("300");

    @org.springframework.transaction.annotation.Transactional
    public HealthProfileResponseDTO actualizarPerfilDeSalud(Long userId, HealthProfileRequestDTO dto) {
        User user = requireUser(userId);
        if (dto.getWeightKg() != null && (dto.getWeightKg().compareTo(MIN_WEIGHT_KG) < 0 || dto.getWeightKg().compareTo(MAX_WEIGHT_KG) > 0))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Peso inválido: " + dto.getWeightKg() + " kg. Rango: 1-500 kg.");
        if (dto.getHeightCm() != null && (dto.getHeightCm().compareTo(MIN_HEIGHT_CM) < 0 || dto.getHeightCm().compareTo(MAX_HEIGHT_CM) > 0))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Altura inválida: " + dto.getHeightCm() + " cm. Rango: 30-300 cm.");
        HealthProfile profile = user.getHealthProfile() != null ? user.getHealthProfile() : new HealthProfile();
        profile.setUser(user);
        if (dto.getBloodType()        != null) profile.setBloodType(dto.getBloodType());
        if (dto.getBloodPressure()    != null) profile.setBloodPressure(dto.getBloodPressure());
        if (dto.getGlucoseLevel()     != null) profile.setGlucoseLevel(dto.getGlucoseLevel());
        if (dto.getCholesterolLevel() != null) profile.setCholesterolLevel(dto.getCholesterolLevel());
        if (dto.getAllergies()         != null) profile.setAllergies(dto.getAllergies());
        if (dto.getWeightKg()         != null) profile.setWeightKg(dto.getWeightKg());
        if (dto.getHeightCm()         != null) profile.setHeightCm(dto.getHeightCm());
        if (profile.getWeightKg() != null && profile.getHeightCm() != null) {
            java.math.BigDecimal hM = profile.getHeightCm().divide(new java.math.BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal bmi = profile.getWeightKg().divide(hM.pow(2, new java.math.MathContext(6)), 2, java.math.RoundingMode.HALF_UP);
            profile.setBmi(bmi);
            profile.setBmiCategory(bmi.compareTo(BMI_UNDERWEIGHT) < 0 ? "Bajo peso" : bmi.compareTo(BMI_NORMAL_MAX) <= 0 ? "Normal" : bmi.compareTo(BMI_OVERWEIGHT_MAX) <= 0 ? "Sobrepeso" : "Obesidad");
        }
        return toHealthProfileDto(healthProfileRepository.save(profile));
    }

    // ── HU07 — Signos vitales + Motor de Reglas Médicas (Stefany) ────────────

    private static final java.math.BigDecimal GLUCOSE_ALERT_THRESHOLD     = new java.math.BigDecimal("126");
    private static final java.math.BigDecimal CHOLESTEROL_ALERT_THRESHOLD = new java.math.BigDecimal("240");
    private static final int SYSTOLIC_ALERT_THRESHOLD  = 140;
    private static final int DIASTOLIC_ALERT_THRESHOLD = 90;

    @org.springframework.transaction.annotation.Transactional
    public HealthProfileResponseDTO registrarSignosVitales(Long userId, VitalSignsRequestDTO dto) {
        User user = requireUser(userId);
        HealthProfile profile = user.getHealthProfile() != null ? user.getHealthProfile() : new HealthProfile();
        profile.setUser(user);
        StringBuilder alert = new StringBuilder();
        boolean critical = false;
        if (dto.getGlucoseLevel() != null) {
            profile.setGlucoseLevel(dto.getGlucoseLevel());
            if (dto.getGlucoseLevel().compareTo(GLUCOSE_ALERT_THRESHOLD) >= 0) { critical = true;
                alert.append("ALERTA: Glucosa ").append(dto.getGlucoseLevel()).append(" mg/dL >= 126 (ADA). "); }
        }
        if (dto.getBloodPressure() != null && !dto.getBloodPressure().isBlank()) {
            profile.setBloodPressure(dto.getBloodPressure());
            String[] p = dto.getBloodPressure().replace("mmHg","").strip().split("/");
            int sys = Integer.parseInt(p[0].strip()), dia = Integer.parseInt(p[1].strip());
            if (sys >= SYSTOLIC_ALERT_THRESHOLD || dia >= DIASTOLIC_ALERT_THRESHOLD) { critical = true;
                alert.append("ALERTA: Presión ").append(dto.getBloodPressure()).append(" >= 140/90 (AHA). "); }
        }
        if (dto.getCholesterolLevel() != null) {
            profile.setCholesterolLevel(dto.getCholesterolLevel());
            if (dto.getCholesterolLevel().compareTo(CHOLESTEROL_ALERT_THRESHOLD) >= 0) { critical = true;
                alert.append("ALERTA: Colesterol ").append(dto.getCholesterolLevel()).append(" mg/dL >= 240 (NCEP). "); }
        }
        if (dto.getAllergies() != null) profile.setAllergies(dto.getAllergies());
        profile.setVitalAlertFlag(critical);
        profile.setAlertMessage(critical ? alert.toString().strip() : null);
        return toHealthProfileDto(healthProfileRepository.save(profile));
    }

    // ── Mappers compartidos ───────────────────────────────────────────────────

    public UserResponseDTO toUserDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRoles().stream()
            .map(r -> r.getName()).findFirst().orElse(null));
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setHealthProfile(user.getHealthProfile() != null
            ? toHealthProfileDto(user.getHealthProfile())
            : null);
        return dto;
    }

    public HealthProfileResponseDTO toHealthProfileDto(HealthProfile hp) {
        HealthProfileResponseDTO dto = new HealthProfileResponseDTO();
        dto.setUserId(hp.getUser().getId());
        dto.setBloodType(hp.getBloodType());
        dto.setBloodPressure(hp.getBloodPressure());
        dto.setGlucoseLevel(hp.getGlucoseLevel());
        dto.setCholesterolLevel(hp.getCholesterolLevel());
        dto.setAllergies(hp.getAllergies());
        dto.setWeightKg(hp.getWeightKg());
        dto.setHeightCm(hp.getHeightCm());
        dto.setBmi(hp.getBmi());
        dto.setBmiCategory(hp.getBmiCategory());
        dto.setVitalAlertFlag(hp.getVitalAlertFlag());
        dto.setAlertMessage(hp.getAlertMessage());
        return dto;
    }

    protected User requireUser(Long id) {
        return userRepository.findByIdWithHealthProfile(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }
}
