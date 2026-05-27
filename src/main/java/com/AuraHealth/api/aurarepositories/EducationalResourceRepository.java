package com.AuraHealth.api.aurarepositories;

import com.AuraHealth.api.auraentities.EducationalResource;
import com.AuraHealth.api.auraentities.ResourceFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationalResourceRepository extends JpaRepository<EducationalResource, Long> {

    List<EducationalResource> findByIsPublishedTrue();

    List<EducationalResource> findByCategoryIgnoreCaseAndIsPublishedTrue(String category);

    List<EducationalResource> findByFormatTypeAndIsPublishedTrue(ResourceFormat formatType);

    @Query("SELECT r FROM EducationalResource r " +
           "WHERE r.isPublished = true " +
           "AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<EducationalResource> buscarPorKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(r) > 0 FROM EducationalResource r WHERE r.id = :id AND r.isPublished = true")
    boolean existsByIdAndIsPublishedTrue(@Param("id") Long id);
}