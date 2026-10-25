package com.jobfitresume.profile;

import com.jobfitresume.certification.CertificationRepository;
import com.jobfitresume.education.EducationRepository;
import com.jobfitresume.experience.ExperienceRepository;
import com.jobfitresume.profile.dto.ProfileCompletenessResponse;
import com.jobfitresume.profile.dto.UserProfileDto;
import com.jobfitresume.project.ProjectRepository;
import com.jobfitresume.security.SecurityUtils;
import com.jobfitresume.skill.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            UserSkillRepository userSkillRepository,
            ProjectRepository projectRepository,
            CertificationRepository certificationRepository) {
        this.userProfileRepository = userProfileRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.userSkillRepository = userSkillRepository;
        this.projectRepository = projectRepository;
        this.certificationRepository = certificationRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile() {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfile(userId));
        return UserProfileDto.fromEntity(profile);
    }

    @Transactional
    public UserProfileDto updateProfile(UserProfileDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile(userId);
                    return p;
                });

        dto.updateEntity(profile);
        UserProfile saved = userProfileRepository.save(profile);
        return UserProfileDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ProfileCompletenessResponse getCompleteness() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);

        Map<String, Boolean> sections = new LinkedHashMap<>();
        List<String> recommendations = new ArrayList<>();
        int score = 0;

        // 1. Personal & Contact info (20 points)
        boolean hasBasicInfo = false;
        if (profileOpt.isPresent()) {
            UserProfile p = profileOpt.get();
            hasBasicInfo = isNotEmpty(p.getProfessionalHeadline())
                    && isNotEmpty(p.getContactEmail())
                    && (isNotEmpty(p.getCity()) || isNotEmpty(p.getCountry()));
        }
        sections.put("basicInfo", hasBasicInfo);
        if (hasBasicInfo) {
            score += 20;
        } else {
            recommendations.add("Complete your headline, contact email, and location");
        }

        // 2. Professional Summary (15 points)
        boolean hasSummary = profileOpt.isPresent() && isNotEmpty(profileOpt.get().getProfessionalSummary())
                && profileOpt.get().getProfessionalSummary().trim().length() >= 50;
        sections.put("summary", hasSummary);
        if (hasSummary) {
            score += 15;
        } else {
            recommendations.add("Add a compelling professional summary (at least 50 characters)");
        }

        // 3. Work Experience (20 points)
        int expCount = experienceRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId).size();
        boolean hasExp = expCount > 0;
        sections.put("experience", hasExp);
        if (hasExp) {
            score += 20;
        } else {
            recommendations.add("Add at least one work experience or internship");
        }

        // 4. Skills (20 points for at least 5 skills)
        int skillCount = userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        boolean hasSkills = skillCount >= 5;
        sections.put("skills", hasSkills);
        if (hasSkills) {
            score += 20;
        } else if (skillCount > 0) {
            score += (skillCount * 4);
            recommendations.add("Add " + (5 - skillCount) + " more skills to showcase your full stack toolkit");
        } else {
            recommendations.add("Add at least 5 technical or professional skills");
        }

        // 5. Education (15 points)
        int eduCount = educationRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId).size();
        boolean hasEdu = eduCount > 0;
        sections.put("education", hasEdu);
        if (hasEdu) {
            score += 15;
        } else {
            recommendations.add("Add your educational background or highest degree");
        }

        // 6. Projects (10 points)
        int projCount = projectRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId).size();
        boolean hasProjects = projCount > 0;
        sections.put("projects", hasProjects);
        if (hasProjects) {
            score += 10;
        } else {
            recommendations.add("Add at least one portfolio project showcasing your technical capabilities");
        }

        // Cap score at 100
        score = Math.min(100, score);

        return new ProfileCompletenessResponse(score, sections, recommendations);
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
