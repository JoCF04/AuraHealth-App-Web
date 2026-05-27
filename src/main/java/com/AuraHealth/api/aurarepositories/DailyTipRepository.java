package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.DailyTip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyTipRepository extends JpaRepository<DailyTip, Long> {

    List<DailyTip> findByIsActiveTrue();
}