package com.jobfitresume.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobAnalysisRepository extends JpaRepository<JobAnalysis, UUID> {
    Optional<JobAnalysis> findTopByJobIdAndUserIdOrderByCreatedAtDesc(UUID jobId, UUID userId);
    List<JobAnalysis> findByJobIdAndUserIdOrderByCreatedAtDesc(UUID jobId, UUID userId);
    void deleteByJobIdAndUserId(UUID jobId, UUID userId);
}
