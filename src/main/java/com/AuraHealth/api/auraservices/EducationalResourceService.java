package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auradtos.*;
import com.AuraHealth.api.auraentities.*;
import com.AuraHealth.api.aurarepositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    // ── HU21/22 — Listar todos los recursos publicados ────────────────────────

    @Transactional(readOnly = true)
    public List<EducationalResourceSummaryDTO> listarTodos() {
        return resourceRepository.findByIsPublishedTrue().stream()
            .map(this::toSummaryDto).collect(Collectors.toList());
    }

    // ── HU29 — Ver detalle de un recurso ─────────────────────────────────────

    @Transactional(readOnly = true)
    public EducationalResourceResponseDTO obtenerPorId(Long id) {
        EducationalResource resource = resourceRepository.findById(id)
            .filter(EducationalResource::getIsPublished)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recurso no encontrado con id: " + id));
        return toFullDto(resource);
    }

    // ── HU26/28 — Buscar por palabra clave ───────────────────────────────────

    @Transactional(readOnly = true)
    public List<EducationalResourceSummaryDTO> buscar(String keyword) {
        return resourceRepository.buscarPorKeyword(keyword).stream()
            .map(this::toSummaryDto).collect(Collectors.toList());
    }

    // ── HU23/27 — Filtrar por categoría ──────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EducationalResourceSummaryDTO> filtrarPorCategoria(String category) {
        return resourceRepository.findByCategoryIgnoreCaseAndIsPublishedTrue(category).stream()
            .map(this::toSummaryDto).collect(Collectors.toList());
    }

    // ── HU04 — Tips del día (3 aleatorios de los activos) ────────────────────

    @Transactional(readOngit checkout developly = true)
    public List<DailyTipResponseDTO> obtenerTipsDelDia() {
        List<DailyTip> tips = dailyTipRepository.findByIsActiveTrue();
        Collections.shuffle(tips);
        return tips.stream().limit(3)
            .map(t -> new DailyTipResponseDTO(t.getId(), t.getContent(), t.getCategory()))
            .collect(Collectors.toList());
    }

    // ── Mappers manuales ──────────────────────────────────────────────────────

    private EducationalResourceSummaryDTO toSummaryDto(EducationalResource r) {
        return new EducationalResourceSummaryDTO(
            r.getId(), r.getTitle(), r.getCategory(),
            r.getDescription(), r.getImageUrl(), r.getAuthor(),
            r.getFormatType() != null ? r.getFormatType().name() : null
        );
    }

    private EducationalResourceResponseDTO toFullDto(EducationalResource r) {
        return new EducationalResourceResponseDTO(
            r.getId(), r.getTitle(), r.getCategory(),
            r.getDescription(), r.getContent(), r.getImageUrl(),
            r.getAuthor(), r.getFormatType() != null ? r.getFormatType().name() : null,
            r.getDownloadUrl(), r.getIsPublished(),
            r.getPublishedAt(), r.getCreatedAt()
        );
    }
}