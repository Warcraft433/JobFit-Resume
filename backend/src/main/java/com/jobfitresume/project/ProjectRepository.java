package com.jobfitresume.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByUserIdOrderByDisplayOrderAscCreatedAtDesc(UUID userId);
    List<Project> findByUserIdAndFeaturedTrue(UUID userId);
    Optional<Project> findByIdAndUserId(UUID id, UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
