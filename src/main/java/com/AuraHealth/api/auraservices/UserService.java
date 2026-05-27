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

    // ── Mappers compartidos (usados por todas las HUs) ────────────────────────

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
