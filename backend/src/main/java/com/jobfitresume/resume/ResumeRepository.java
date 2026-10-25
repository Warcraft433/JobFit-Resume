package com.jobfitresume.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByUserIdOrderByUpdatedAtDesc(UUID userId);
    Optional<Resume> findByIdAndUserId(UUID id, UUID userId);
    List<Resume> findByUserIdAndJobId(UUID userId, UUID jobId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
