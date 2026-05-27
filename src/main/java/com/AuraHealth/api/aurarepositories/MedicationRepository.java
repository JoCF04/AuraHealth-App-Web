package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    /** HU09 — Lista todos los medicamentos del usuario, ordenados por nombre ASC. */
    List<Medication> findByUserIdOrderByNameAsc(Long userId);

    /** Valida que el medicamento pertenezca al usuario (ownership check). */
    Optional<Medication> findByIdAndUserId(Long id, Long userId);

    /** HU08 — Verifica duplicado case-insensitive antes de crear (Stefany). */
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    /** Para edición futura: verifica duplicado excluyendo el propio registro. */
    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);
}