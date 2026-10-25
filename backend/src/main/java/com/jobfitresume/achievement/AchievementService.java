package com.jobfitresume.achievement;

import com.jobfitresume.achievement.dto.AchievementDto;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getAchievements() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return achievementRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AchievementDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AchievementDto getAchievementById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Achievement achievement = achievementRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found"));
        return AchievementDto.fromEntity(achievement);
    }

    @Transactional
    public AchievementDto createAchievement(AchievementDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Achievement entity = new Achievement();
        entity.setUserId(userId);
        dto.applyToEntity(entity);
        Achievement saved = achievementRepository.save(entity);
        return AchievementDto.fromEntity(saved);
    }

    @Transactional
    public AchievementDto updateAchievement(UUID id, AchievementDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Achievement entity = achievementRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found"));
        dto.applyToEntity(entity);
        Achievement updated = achievementRepository.save(entity);
        return AchievementDto.fromEntity(updated);
    }

    @Transactional
    public void deleteAchievement(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Achievement entity = achievementRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found"));
        achievementRepository.delete(entity);
    }
}
