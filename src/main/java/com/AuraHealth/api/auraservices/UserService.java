package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auraentities.*;
import com.AuraHealth.api.aurarepositories.*;
import com.AuraHealth.api.auradtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("es", "en");
    private static final BigDecimal BMI_UNDERWEIGHT    = new BigDecimal("18.5");
    private static final BigDecimal BMI_NORMAL_MAX     = new BigDecimal("24.9");
    private static final BigDecimal BMI_OVERWEIGHT_MAX = new BigDecimal("29.9");
    private static final BigDecimal MIN_WEIGHT_KG = new BigDecimal("1");
    private static final BigDecimal MAX_WEIGHT_KG = new BigDecimal("500");
    private static final BigDecimal MIN_HEIGHT_CM = new BigDecimal("30");
    private static final BigDecimal MAX_HEIGHT_CM = new BigDecimal("300");
    private static final BigDecimal GLUCOSE_ALERT_THRESHOLD     = new BigDecimal("126");
    private static final BigDecimal CHOLESTEROL_ALERT_THRESHOLD = new BigDecimal("240");
    private static final int SYSTOLIC_ALERT_THRESHOLD  = 140;
    private static final int DIASTOLIC_ALERT_THRESHOLD = 90;

    private final UserRepository          userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final RoleRepository          roleRepository;
    private final PasswordEncoder         passwordEncoder;

    public UserService(UserRepository userRepository, HealthProfileRepository healthProfileRepository,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO registrarUsuario(UserRegistrationRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo '" + dto.getEmail() + "' ya está registrado");
        String roleName = "ROLE_" + (dto.getRole() != null ? dto.getRole() : "USER");
        Role userRole = roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
        User user = new User();
        user.setFirstName(dto.getFirstName()); user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase().strip());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setGender(dto.getGender()); user.setIsEmailVerified(true);
        user.setPreferredLanguage(dto.getPreferredLanguage() != null ? dto.getPreferredLanguage() : "es");
        user.setRoles(new HashSet<>(Set.of(userRole)));
        if (dto.getBirthDate() != null && !dto.getBirthDate().isBlank()) user.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        return toUserDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorId(Long id) { return toUserDto(requireUser(id)); }

    @Transactional
    public HealthProfileResponseDTO actualizarPerfilDeSalud(Long userId, HealthProfileRequestDTO dto) {
        User user = requireUser(userId);
        if (dto.getWeightKg() != null && (dto.getWeightKg().compareTo(MIN_WEIGHT_KG) < 0 || dto.getWeightKg().compareTo(MAX_WEIGHT_KG) > 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Peso inválido.");
        if (dto.getHeightCm() != null && (dto.getHeightCm().compareTo(MIN_HEIGHT_CM) < 0 || dto.getHeightCm().compareTo(MAX_HEIGHT_CM) > 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Altura inválida.");
        HealthProfile p = user.getHealthProfile() != null ? user.getHealthProfile() : new HealthProfile();
        p.setUser(user);
        if (dto.getBloodType()        != null) p.setBloodType(dto.getBloodType());
        if (dto.getBloodPressure()    != null) p.setBloodPressure(dto.getBloodPressure());
        if (dto.getGlucoseLevel()     != null) p.setGlucoseLevel(dto.getGlucoseLevel());
        if (dto.getCholesterolLevel() != null) p.setCholesterolLevel(dto.getCholesterolLevel());
        if (dto.getAllergies()         != null) p.setAllergies(dto.getAllergies());
        if (dto.getWeightKg()         != null) p.setWeightKg(dto.getWeightKg());
        if (dto.getHeightCm()         != null) p.setHeightCm(dto.getHeightCm());
        recalculateBmi(p);
        return toHealthProfileDto(healthProfileRepository.save(p));
    }

    @Transactional
    public HealthProfileResponseDTO registrarSignosVitales(Long userId, VitalSignsRequestDTO dto) {
        User user = requireUser(userId);
        HealthProfile p = user.getHealthProfile() != null ? user.getHealthProfile() : new HealthProfile();
        p.setUser(user);
        StringBuilder alert = new StringBuilder(); boolean critical = false;
        if (dto.getGlucoseLevel() != null) { p.setGlucoseLevel(dto.getGlucoseLevel());
            if (dto.getGlucoseLevel().compareTo(GLUCOSE_ALERT_THRESHOLD) >= 0) { critical = true; alert.append("ALERTA: Glucosa ").append(dto.getGlucoseLevel()).append(" mg/dL >= 126. "); } }
        if (dto.getBloodPressure() != null && !dto.getBloodPressure().isBlank()) { p.setBloodPressure(dto.getBloodPressure());
            String[] parts = dto.getBloodPressure().replace("mmHg","").strip().split("/");
            int sys = Integer.parseInt(parts[0].strip()), dia = Integer.parseInt(parts[1].strip());
            if (sys >= SYSTOLIC_ALERT_THRESHOLD || dia >= DIASTOLIC_ALERT_THRESHOLD) { critical = true; alert.append("ALERTA: Presión ").append(dto.getBloodPressure()).append(" >= 140/90. "); } }
        if (dto.getCholesterolLevel() != null) { p.setCholesterolLevel(dto.getCholesterolLevel());
            if (dto.getCholesterolLevel().compareTo(CHOLESTEROL_ALERT_THRESHOLD) >= 0) { critical = true; alert.append("ALERTA: Colesterol ").append(dto.getCholesterolLevel()).append(" >= 240. "); } }
        if (dto.getAllergies() != null) p.setAllergies(dto.getAllergies());
        p.setVitalAlertFlag(critical); p.setAlertMessage(critical ? alert.toString().strip() : null);
        recalculateBmi(p);
        return toHealthProfileDto(healthProfileRepository.save(p));
    }

    // ── HU06 — Cambiar idioma preferido (Masiel) ──────────────────────────────

    @Transactional
    public UserResponseDTO cambiarIdioma(Long id, String lang) {
        if (!SUPPORTED_LANGUAGES.contains(lang))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Código de idioma inválido: '" + lang + "'. Valores aceptados: " + SUPPORTED_LANGUAGES);
        User user = requireUser(id);
        user.setPreferredLanguage(lang);
        return toUserDto(userRepository.save(user));
    }

    private void recalculateBmi(HealthProfile p) {
        if (p.getWeightKg() == null || p.getHeightCm() == null) return;
        BigDecimal hM = p.getHeightCm().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal bmi = p.getWeightKg().divide(hM.pow(2, new MathContext(6)), 2, RoundingMode.HALF_UP);
        p.setBmi(bmi);
        p.setBmiCategory(bmi.compareTo(BMI_UNDERWEIGHT)<0?"Bajo peso":bmi.compareTo(BMI_NORMAL_MAX)<=0?"Normal":bmi.compareTo(BMI_OVERWEIGHT_MAX)<=0?"Sobrepeso":"Obesidad");
    }

    public UserResponseDTO toUserDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId()); dto.setFirstName(user.getFirstName()); dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail()); dto.setRole(user.getRoles().stream().map(r->r.getName()).findFirst().orElse(null));
        dto.setBirthDate(user.getBirthDate()); dto.setGender(user.getGender());
        dto.setIsEmailVerified(user.getIsEmailVerified()); dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setHealthProfile(user.getHealthProfile()!=null?toHealthProfileDto(user.getHealthProfile()):null);
        return dto;
    }

    public HealthProfileResponseDTO toHealthProfileDto(HealthProfile hp) {
        HealthProfileResponseDTO dto = new HealthProfileResponseDTO();
        dto.setUserId(hp.getUser().getId()); dto.setBloodType(hp.getBloodType());
        dto.setBloodPressure(hp.getBloodPressure()); dto.setGlucoseLevel(hp.getGlucoseLevel());
        dto.setCholesterolLevel(hp.getCholesterolLevel()); dto.setAllergies(hp.getAllergies());
        dto.setWeightKg(hp.getWeightKg()); dto.setHeightCm(hp.getHeightCm());
        dto.setBmi(hp.getBmi()); dto.setBmiCategory(hp.getBmiCategory());
        dto.setVitalAlertFlag(hp.getVitalAlertFlag()); dto.setAlertMessage(hp.getAlertMessage());
        return dto;
    }

    protected User requireUser(Long id) {
        return userRepository.findByIdWithHealthProfile(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }
}