package com.jobfitresume.resume;

import com.jobfitresume.achievement.Achievement;
import com.jobfitresume.achievement.AchievementRepository;
import com.jobfitresume.certification.Certification;
import com.jobfitresume.certification.CertificationRepository;
import com.jobfitresume.education.Education;
import com.jobfitresume.education.EducationRepository;
import com.jobfitresume.experience.Experience;
import com.jobfitresume.experience.ExperienceRepository;
import com.jobfitresume.job.Job;
import com.jobfitresume.job.parser.SkillDictionary;
import com.jobfitresume.profile.UserProfile;
import com.jobfitresume.profile.UserProfileRepository;
import com.jobfitresume.project.Project;
import com.jobfitresume.project.ProjectRepository;
import com.jobfitresume.resume.dto.GenerateResumeRequest;
import com.jobfitresume.resume.dto.ResumeContentModel;
import com.jobfitresume.skill.Skill;
import com.jobfitresume.skill.SkillRepository;
import com.jobfitresume.skill.UserSkill;
import com.jobfitresume.skill.UserSkillRepository;
import com.jobfitresume.user.User;
import com.jobfitresume.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeTailoringService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final CertificationRepository certificationRepository;
    private final AchievementRepository achievementRepository;
    private final SkillDictionary skillDictionary;

    public ResumeTailoringService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            ExperienceRepository experienceRepository,
            EducationRepository educationRepository,
            ProjectRepository projectRepository,
            UserSkillRepository userSkillRepository,
            SkillRepository skillRepository,
            CertificationRepository certificationRepository,
            AchievementRepository achievementRepository,
            SkillDictionary skillDictionary) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
        this.userSkillRepository = userSkillRepository;
        this.skillRepository = skillRepository;
        this.certificationRepository = certificationRepository;
        this.achievementRepository = achievementRepository;
        this.skillDictionary = skillDictionary;
    }

    public ResumeContentModel buildTailoredContent(UUID userId, Job targetJob, GenerateResumeRequest request) {
        ResumeContentModel model = new ResumeContentModel();

        // 1. Contact & Header
        User user = userRepository.findById(userId).orElse(null);
        Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);

        ResumeContentModel.ContactInfo contact = model.getContact();
        if (user != null) {
            contact.fullName = user.getFullName();
            contact.email = user.getEmail();
        }

        if (profileOpt.isPresent()) {
            UserProfile p = profileOpt.get();
            if (p.getContactEmail() != null && !p.getContactEmail().isEmpty()) {
                contact.email = p.getContactEmail();
            }
            contact.headline = p.getProfessionalHeadline() != null ? p.getProfessionalHeadline()
                    : (targetJob != null ? targetJob.getJobTitle() : "Software Engineer");
            contact.phone = p.getPhone() != null ? p.getPhone() : "";
            String loc = "";
            if (p.getCity() != null) loc += p.getCity();
            if (p.getCountry() != null) loc += (loc.isEmpty() ? "" : ", ") + p.getCountry();
            contact.location = loc;
            contact.linkedinUrl = p.getLinkedinUrl() != null ? p.getLinkedinUrl() : "";
            contact.githubUrl = p.getGithubUrl() != null ? p.getGithubUrl() : "";
            contact.portfolioUrl = p.getPortfolioUrl() != null ? p.getPortfolioUrl() : "";
            contact.customLinks = p.getCustomLinks() != null ? p.getCustomLinks() : "";

            // Tailored Summary
            String baseSummary = p.getProfessionalSummary();
            if (baseSummary != null && !baseSummary.trim().isEmpty()) {
                model.setProfessionalSummary(baseSummary.trim());
            } else if (targetJob != null) {
                model.setProfessionalSummary("Results-driven professional with strong technical foundations and hands-on full stack development experience. Dedicated to architecting robust, scalable software solutions aligned with "
                        + targetJob.getCompany() + "'s engineering standards as a " + targetJob.getJobTitle() + ".");
            }
        }

        // 2. Skills Tailoring (Prioritize skills that match the target job description)
        List<UserSkill> userSkills = userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Set<SkillDictionary.SkillMeta> jobSkills = targetJob != null
                ? skillDictionary.extractSkillsFromText(targetJob.getJobDescription())
                : Collections.emptySet();
        Set<String> targetSkillNames = jobSkills.stream()
                .map(s -> s.getDisplayName().toLowerCase())
                .collect(Collectors.toSet());

        List<String> primary = new ArrayList<>();
        List<String> secondary = new ArrayList<>();

        for (UserSkill us : userSkills) {
            String skillName = us.getCustomSkillName();
            if (us.getSkillId() != null) {
                Optional<Skill> sk = skillRepository.findById(us.getSkillId());
                if (sk.isPresent()) skillName = sk.get().getName();
            }

            if (skillName != null && !skillName.trim().isEmpty()) {
                if (targetSkillNames.contains(skillName.trim().toLowerCase())) {
                    primary.add(skillName.trim());
                } else {
                    secondary.add(skillName.trim());
                }
            }
        }

        if (primary.isEmpty() && !secondary.isEmpty()) {
            primary.addAll(secondary.subList(0, Math.min(6, secondary.size())));
            secondary = secondary.subList(Math.min(6, secondary.size()), secondary.size());
        }

        model.setPrimarySkills(primary);
        model.setSecondarySkills(secondary);

        // 3. Work Experience Tailoring
        List<Experience> rawExp = experienceRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        int maxExp = (request != null && request.getMaxExperiences() != null) ? request.getMaxExperiences() : 4;
        List<ResumeContentModel.ExperienceItem> expItems = new ArrayList<>();

        for (int i = 0; i < Math.min(rawExp.size(), maxExp); i++) {
            Experience e = rawExp.get(i);
            ResumeContentModel.ExperienceItem item = new ResumeContentModel.ExperienceItem();
            item.employer = e.getEmployer();
            item.jobTitle = e.getJobTitle();
            item.location = e.getLocation();
            item.startDate = e.getStartDate();
            item.endDate = e.getEndDate();
            item.currentRole = e.isCurrentRole();
            item.description = e.getDescription();

            // Extract bullet points from achievements/description
            if (e.getAchievements() != null && !e.getAchievements().trim().isEmpty()) {
                String[] lines = e.getAchievements().split("\\r?\\n");
                for (String line : lines) {
                    String clean = line.replaceAll("^[•\\-*\\d.]+\\s*", "").trim();
                    if (!clean.isEmpty()) item.bulletPoints.add(clean);
                }
            }
            if (e.getTechnologies() != null && !e.getTechnologies().trim().isEmpty()) {
                item.technologies = Arrays.stream(e.getTechnologies().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            expItems.add(item);
        }
        model.setExperiences(expItems);

        // 4. Projects Tailoring (Rank projects by technology overlap with target job)
        List<Project> allProjects = projectRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        if (targetJob != null) {
            allProjects.sort((p1, p2) -> {
                int score1 = calculateProjectRelevance(p1, targetJob.getJobDescription());
                int score2 = calculateProjectRelevance(p2, targetJob.getJobDescription());
                return Integer.compare(score2, score1); // descending
            });
        }

        int maxProj = (request != null && request.getMaxProjects() != null) ? request.getMaxProjects() : 3;
        List<ResumeContentModel.ProjectItem> projItems = new ArrayList<>();
        for (int i = 0; i < Math.min(allProjects.size(), maxProj); i++) {
            Project p = allProjects.get(i);
            ResumeContentModel.ProjectItem item = new ResumeContentModel.ProjectItem();
            item.name = p.getName();
            item.category = p.getCategory();
            item.description = p.getDescription();
            item.technicalStack = p.getTechnicalStack();
            item.responsibilities = p.getResponsibilities();
            item.outcomes = p.getOutcomes();
            item.githubUrl = p.getGithubUrl();
            item.demoUrl = p.getDemoUrl();
            projItems.add(item);
        }
        model.setProjects(projItems);

        // 5. Education
        List<Education> rawEdu = educationRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        List<ResumeContentModel.EducationItem> eduItems = new ArrayList<>();
        for (Education ed : rawEdu) {
            ResumeContentModel.EducationItem item = new ResumeContentModel.EducationItem();
            item.institution = ed.getInstitution();
            item.degree = ed.getDegree();
            item.fieldOfStudy = ed.getFieldOfStudy();
            item.startDate = ed.getStartDate();
            item.endDate = ed.getEndDate();
            item.currentlyStudying = ed.isCurrentlyStudying();
            item.grade = ed.isShowGradeOnResume() && ed.getGradeValue() != null ? ed.getGradeValue() : "";
            eduItems.add(item);
        }
        model.setEducations(eduItems);

        // 6. Certifications
        List<Certification> rawCert = certificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ResumeContentModel.CertificationItem> certItems = new ArrayList<>();
        for (Certification c1 : rawCert) {
            ResumeContentModel.CertificationItem item = new ResumeContentModel.CertificationItem();
            item.name = c1.getName();
            item.issuer = c1.getIssuer();
            item.issueDate = c1.getIssueDate();
            item.credentialUrl = c1.getCredentialUrl();
            certItems.add(item);
        }
        model.setCertifications(certItems);

        // 7. Achievements
        List<Achievement> rawAch = achievementRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ResumeContentModel.AchievementItem> achItems = new ArrayList<>();
        for (Achievement a : rawAch) {
            ResumeContentModel.AchievementItem item = new ResumeContentModel.AchievementItem();
            item.title = a.getTitle();
            item.issuer = a.getIssuer();
            item.achievementDate = a.getAchievementDate();
            item.description = a.getDescription();
            achItems.add(item);
        }
        model.setAchievements(achItems);

        return model;
    }

    private int calculateProjectRelevance(Project p, String jobDesc) {
        if (jobDesc == null) return 0;
        int score = 0;
        String lowerDesc = jobDesc.toLowerCase();
        if (p.getTechnicalStack() != null) {
            for (String tech : p.getTechnicalStack().split("[,/ ]+")) {
                if (!tech.trim().isEmpty() && lowerDesc.contains(tech.trim().toLowerCase())) {
                    score += 5;
                }
            }
        }
        if (p.getDescription() != null && lowerDesc.contains(p.getDescription().toLowerCase())) {
            score += 2;
        }
        return score;
    }
}
