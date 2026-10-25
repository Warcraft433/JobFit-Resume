package com.jobfitresume.skill;

import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.security.SecurityUtils;
import com.jobfitresume.skill.dto.UserSkillDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    public SkillService(SkillRepository skillRepository, UserSkillRepository userSkillRepository) {
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllCanonicalSkills() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserSkillDto> getUserSkills() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(entity -> {
                    UserSkillDto dto = UserSkillDto.fromEntity(entity);
                    if (entity.getSkillId() != null) {
                        skillRepository.findById(entity.getSkillId())
                                .ifPresent(s -> dto.setCanonicalName(s.getCanonicalName()));
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional
    public UserSkillDto addUserSkill(UserSkillDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserSkill entity = new UserSkill();
        entity.setUserId(userId);
        dto.applyToEntity(entity);

        // Attempt automatic match against canonical skill catalog if not explicitly set
        if (entity.getSkillId() == null && dto.getSkillName() != null) {
            String normalized = dto.getSkillName().trim().toLowerCase().replaceAll("[^a-z0-9]", "_");
            Optional<Skill> matchedSkill = skillRepository.findByCanonicalName(normalized);
            matchedSkill.ifPresent(s -> entity.setSkillId(s.getId()));
        }

        UserSkill saved = userSkillRepository.save(entity);
        return UserSkillDto.fromEntity(saved);
    }

    @Transactional
    public UserSkillDto updateUserSkill(UUID id, UserSkillDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserSkill entity = userSkillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found in user profile"));
        dto.applyToEntity(entity);
        UserSkill updated = userSkillRepository.save(entity);
        return UserSkillDto.fromEntity(updated);
    }

    @Transactional
    public void deleteUserSkill(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserSkill entity = userSkillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found in user profile"));
        userSkillRepository.delete(entity);
    }
}
