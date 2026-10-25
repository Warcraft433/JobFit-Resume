package com.jobfitresume.certification;

import com.jobfitresume.certification.dto.CertificationDto;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CertificationService {

    private final CertificationRepository certificationRepository;

    public CertificationService(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    @Transactional(readOnly = true)
    public List<CertificationDto> getCertifications() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return certificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(CertificationDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificationDto getCertificationById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Certification cert = certificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));
        return CertificationDto.fromEntity(cert);
    }

    @Transactional
    public CertificationDto createCertification(CertificationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Certification entity = new Certification();
        entity.setUserId(userId);
        dto.applyToEntity(entity);
        Certification saved = certificationRepository.save(entity);
        return CertificationDto.fromEntity(saved);
    }

    @Transactional
    public CertificationDto updateCertification(UUID id, CertificationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Certification entity = certificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));
        dto.applyToEntity(entity);
        Certification updated = certificationRepository.save(entity);
        return CertificationDto.fromEntity(updated);
    }

    @Transactional
    public void deleteCertification(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Certification entity = certificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));
        certificationRepository.delete(entity);
    }
}
