package com.jobfitresume.experience;

import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.experience.dto.ExperienceDto;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    @Transactional(readOnly = true)
    public List<ExperienceDto> getExperienceList() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return experienceRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId)
                .stream()
                .map(ExperienceDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienceDto getExperienceById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Experience experience = experienceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience record not found"));
        return ExperienceDto.fromEntity(experience);
    }

    @Transactional
    public ExperienceDto createExperience(ExperienceDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Experience entity = new Experience();
        entity.setUserId(userId);
        dto.applyToEntity(entity);
        Experience saved = experienceRepository.save(entity);
        return ExperienceDto.fromEntity(saved);
    }

    @Transactional
    public ExperienceDto updateExperience(UUID id, ExperienceDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Experience entity = experienceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience record not found"));
        dto.applyToEntity(entity);
        Experience updated = experienceRepository.save(entity);
        return ExperienceDto.fromEntity(updated);
    }

    @Transactional
    public void deleteExperience(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Experience entity = experienceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience record not found"));
        experienceRepository.delete(entity);
    }
}
