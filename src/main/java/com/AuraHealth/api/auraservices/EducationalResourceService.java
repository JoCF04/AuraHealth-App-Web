package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auradtos.*;
import com.AuraHealth.api.auraentities.*;
import com.AuraHealth.api.aurarepositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EducationalResourceService {

    private final EducationalResourceRepository resourceRepository;
    private final DailyTipRepository            dailyTipRepository;

    public EducationalResourceService(EducationalResourceRepository resourceRepository,
                                      DailyTipRepository dailyTipRepository) {
        this.resourceRepository = resourceRepository;
        this.dailyTipRepository = dailyTipRepository;
    }

    // ── HU21/HU22 — Listar todos los recursos publicados ─────────────────────

    @Transactional(readOnly = true)
    public List<EducationalResourceSummaryDTO> listarTodos() {
        return resourceRepository.findByIsPublishedTrue().stream()
            .map(this::toSummaryDto)
            .collect(Collectors.toList());
    }

    // ── HU04 — Tips del día (3 aleatorios de los activos) ────────────────────

    @Transactional(readOngit checkout developly = true)
    public List<DailyTipResponseDTO> obtenerTipsDelDia() {
        List<DailyTip> tips = dailyTipRepository.findByIsActiveTrue();
        Collections.shuffle(tips);
        return tips.stream()
            .limit(3)
            .map(t -> new DailyTipResponseDTO(t.getId(), t.getContent(), t.getCategory()))
            .collect(Collectors.toList());
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private EducationalResourceSummaryDTO toSummaryDto(EducationalResource r) {
        return new EducationalResourceSummaryDTO(
            r.getId(), r.getTitle(), r.getCategory(),
            r.getDescription(), r.getImageUrl(), r.getAuthor(),
            r.getFormatType() != null ? r.getFormatType().name() : null
        );
    }
}
