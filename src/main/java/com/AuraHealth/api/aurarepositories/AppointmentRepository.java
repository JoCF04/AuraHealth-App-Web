package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserIdOrderByAppointmentDateAsc(Long userId);

    List<Appointment> findByUserIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
            Long userId, LocalDate today);

    Optional<Appointment> findByIdAndUserId(Long id, Long userId);
}