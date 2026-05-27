package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Carga usuario + healthProfile en una sola query (evita N+1). */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.healthProfile WHERE u.id = :id")
    Optional<User> findByIdWithHealthProfile(@Param("id") Long id);
}