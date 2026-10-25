package com.jobfitresume.education;

import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.education.dto.EducationDto;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @Transactional(readOnly = true)
    public List<EducationDto> getEducationList() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return educationRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId)
                .stream()
                .map(EducationDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EducationDto getEducationById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Education education = educationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));
        return EducationDto.fromEntity(education);
    }

    @Transactional
    public EducationDto createEducation(EducationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Education entity = new Education();
        entity.setUserId(userId);
        dto.applyToEntity(entity);
        Education saved = educationRepository.save(entity);
        return EducationDto.fromEntity(saved);
    }

    @Transactional
    public EducationDto updateEducation(UUID id, EducationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Education entity = educationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));
        dto.applyToEntity(entity);
        Education updated = educationRepository.save(entity);
        return EducationDto.fromEntity(updated);
    }

    @Transactional
    public void deleteEducation(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Education entity = educationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));
        educationRepository.delete(entity);
    }
}
