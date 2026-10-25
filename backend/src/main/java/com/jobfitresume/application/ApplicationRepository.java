package com.jobfitresume.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByUserIdOrderByUpdatedAtDesc(UUID userId);
    List<Application> findByUserIdAndStatusOrderByUpdatedAtDesc(UUID userId, String status);
    Optional<Application> findByIdAndUserId(UUID id, UUID userId);
    Optional<Application> findByUserIdAndJobId(UUID userId, UUID jobId);
    void deleteByIdAndUserId(UUID id, UUID userId);
}
