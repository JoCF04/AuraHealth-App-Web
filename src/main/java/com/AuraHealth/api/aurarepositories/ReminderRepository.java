package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserIdOrderByScheduledDateAsc(Long userId);

    List<Reminder> findByUserIdAndReminderTypeIgnoreCaseOrderByScheduledDateAsc(
            Long userId, String reminderType);

    /** Pendientes: no completados con fecha >= hoy. */
    List<Reminder> findByUserIdAndIsDoneFalseAndScheduledDateGreaterThanEqualOrderByScheduledDateAsc(
            Long userId, LocalDate today);

    /** Vencidos: no completados con fecha < hoy. */
    List<Reminder> findByUserIdAndIsDoneFalseAndScheduledDateBeforeOrderByScheduledDateDesc(
            Long userId, LocalDate today);

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(r) FROM Reminder r " +
           "WHERE r.user.id = :userId " +
           "AND r.isDone = false " +
           "AND r.scheduledDate = :today")
    Long countPendientesHoy(@Param("userId") Long userId,
                            @Param("today") LocalDate today);
}