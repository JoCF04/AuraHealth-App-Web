package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
}