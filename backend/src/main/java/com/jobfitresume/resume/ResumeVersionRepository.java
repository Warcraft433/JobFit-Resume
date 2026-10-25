package com.jobfitresume.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, UUID> {
    List<ResumeVersion> findByResumeIdOrderByVersionNumberDesc(UUID resumeId);
    Optional<ResumeVersion> findTopByResumeIdOrderByVersionNumberDesc(UUID resumeId);
    Optional<ResumeVersion> findByResumeIdAndVersionNumber(UUID resumeId, Integer versionNumber);
    void deleteByResumeId(UUID resumeId);
}
