package com.jobfitresume.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRequirementRepository extends JpaRepository<JobRequirement, UUID> {
    List<JobRequirement> findByJobAnalysisId(UUID jobAnalysisId);
    void deleteByJobAnalysisId(UUID jobAnalysisId);
}
