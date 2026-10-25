package com.jobfitresume.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UUID> {
    List<UserSkill> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<UserSkill> findByUserIdAndCategory(UUID userId, String category);
    Optional<UserSkill> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByUserIdAndCustomSkillNameIgnoreCase(UUID userId, String customSkillName);
}
