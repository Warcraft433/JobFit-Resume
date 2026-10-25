package com.jobfitresume.job;

import com.jobfitresume.education.Education;
import com.jobfitresume.education.EducationRepository;
import com.jobfitresume.experience.Experience;
import com.jobfitresume.experience.ExperienceRepository;
import com.jobfitresume.job.dto.JobAnalysisResponse;
import com.jobfitresume.job.dto.JobRequirementDto;
import com.jobfitresume.job.dto.LearningRecommendationDto;
import com.jobfitresume.job.dto.SkillsGapResponse;
import com.jobfitresume.job.parser.JobDescriptionParser;
import com.jobfitresume.job.parser.SkillDictionary;
import com.jobfitresume.project.Project;
import com.jobfitresume.project.ProjectRepository;
import com.jobfitresume.skill.Skill;
import com.jobfitresume.skill.SkillRepository;
import com.jobfitresume.skill.UserSkill;
import com.jobfitresume.skill.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class JobMatchingService {

    private final JobDescriptionParser jobDescriptionParser;
    private final SkillDictionary skillDictionary;
    private final UserSkillRepository userSkillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    public JobMatchingService(
            JobDescriptionParser jobDescriptionParser,
            SkillDictionary skillDictionary,
            UserSkillRepository userSkillRepository,
            ExperienceRepository experienceRepository,
            EducationRepository educationRepository,
            ProjectRepository projectRepository,
            SkillRepository skillRepository) {
        this.jobDescriptionParser = jobDescriptionParser;
        this.skillDictionary = skillDictionary;
        this.userSkillRepository = userSkillRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
    }

    public static class MatchingResult {
        public double overallScore;
        public Map<String, Double> breakdown;
        public List<JobRequirement> requirementsToPersist = new ArrayList<>();
        public List<JobRequirementDto> requirementDtos = new ArrayList<>();
        public List<String> matchedSkills = new ArrayList<>();
        public List<String> missingSkills = new ArrayList<>();
        public List<String> missingPreferredSkills = new ArrayList<>();
        public String experienceAssessment;
        public String educationAssessment;
        public List<String> responsibilities;
        public List<LearningRecommendationDto> recommendations = new ArrayList<>();
    }

    public MatchingResult evaluate(UUID userId, Job job) {
        JobDescriptionParser.ParsedJobData parsed = jobDescriptionParser.parse(job.getJobDescription());

        // 1. Fetch user data
        List<UserSkill> userSkills = userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Experience> userExperiences = experienceRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        List<Education> userEducations = educationRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);
        List<Project> userProjects = projectRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId);

        // Normalize candidate's skills set
        Set<String> candidateSkillCanonicalNames = new HashSet<>();
        for (UserSkill us : userSkills) {
            if (us.getSkillId() != null) {
                skillRepository.findById(us.getSkillId())
                        .ifPresent(s -> candidateSkillCanonicalNames.add(s.getCanonicalName().toLowerCase()));
            }
            if (us.getCustomSkillName() != null) {
                skillDictionary.findByAliasOrName(us.getCustomSkillName())
                        .ifPresent(meta -> candidateSkillCanonicalNames.add(meta.getCanonicalName().toLowerCase()));
            }
        }

        MatchingResult res = new MatchingResult();
        res.responsibilities = parsed.getResponsibilities();

        // 2. Evaluate Required Skills (50% of weight)
        Set<SkillDictionary.SkillMeta> allRequired = new LinkedHashSet<>(parsed.getRequiredSkills());
        if (allRequired.isEmpty() && !parsed.getPreferredSkills().isEmpty()) {
            allRequired.addAll(parsed.getPreferredSkills());
        }

        int totalReqSkills = allRequired.size();
        int matchedReqCount = 0;

        for (SkillDictionary.SkillMeta req : allRequired) {
            boolean matched = candidateSkillCanonicalNames.contains(req.getCanonicalName().toLowerCase());
            if (matched) {
                matchedReqCount++;
                res.matchedSkills.add(req.getDisplayName());
            } else {
                res.missingSkills.add(req.getDisplayName());
                res.recommendations.add(createRecommendation(req, "HIGH (Required Skill)"));
            }

            JobRequirement entity = new JobRequirement();
            entity.setRequirementText("Proficiency in " + req.getDisplayName());
            entity.setRequirementType("SKILL");
            entity.setImportance("REQUIRED");
            entity.setMatched(matched);
            entity.setEvidenceJson(matched ? "Matched profile skill: " + req.getDisplayName() : "Not found in profile skills");
            res.requirementsToPersist.add(entity);

            JobRequirementDto dto = new JobRequirementDto();
            dto.setRequirementText(entity.getRequirementText());
            dto.setCanonicalSkillName(req.getDisplayName());
            dto.setRequirementType(entity.getRequirementType());
            dto.setImportance(entity.getImportance());
            dto.setMatched(matched);
            dto.setEvidence(entity.getEvidenceJson());
            res.requirementDtos.add(dto);
        }

        for (SkillDictionary.SkillMeta pref : parsed.getPreferredSkills()) {
            boolean matched = candidateSkillCanonicalNames.contains(pref.getCanonicalName().toLowerCase());
            if (!matched && !res.missingSkills.contains(pref.getDisplayName())) {
                res.missingPreferredSkills.add(pref.getDisplayName());
                res.recommendations.add(createRecommendation(pref, "MEDIUM (Preferred Skill)"));
            }
        }

        double skillScore = totalReqSkills > 0 ? ((double) matchedReqCount / totalReqSkills) * 50.0 : 45.0;

        // 3. Evaluate Experience (25% of weight)
        int requiredYears = parsed.getMinExperienceYears();
        int candidateYears = calculateCandidateExperienceYears(userExperiences);
        double experienceScore;
        if (requiredYears <= 0) {
            experienceScore = 25.0;
            res.experienceAssessment = "No strict minimum experience threshold specified. Candidate profile has "
                    + candidateYears + " year(s) of documented experience.";
        } else if (candidateYears >= requiredYears) {
            experienceScore = 25.0;
            res.experienceAssessment = "Meets or exceeds requirement: " + candidateYears + " year(s) of experience (required: " + requiredYears + "+ years).";
        } else {
            experienceScore = Math.max(5.0, ((double) candidateYears / requiredYears) * 25.0);
            res.experienceAssessment = "Experience gap: " + candidateYears + " year(s) documented vs " + requiredYears + "+ years required.";
        }

        // 4. Evaluate Education (15% of weight)
        double educationScore = 15.0;
        if (userEducations.isEmpty()) {
            educationScore = 5.0;
            res.educationAssessment = "No education records found in profile. Suggested degree: " + parsed.getDegreeRequirement();
        } else {
            res.educationAssessment = "Candidate has " + userEducations.size() + " education record(s). Target requirement: " + parsed.getDegreeRequirement();
        }

        // 5. Keywords & Project Relevance (10% of weight)
        double keywordScore = 10.0;
        if (userProjects.isEmpty()) {
            keywordScore = 5.0;
        }

        double totalScore = Math.min(100.0, Math.round((skillScore + experienceScore + educationScore + keywordScore) * 10.0) / 10.0);
        res.overallScore = totalScore;

        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("skills", Math.round(skillScore * 10.0) / 10.0);
        breakdown.put("experience", Math.round(experienceScore * 10.0) / 10.0);
        breakdown.put("education", Math.round(educationScore * 10.0) / 10.0);
        breakdown.put("projectsKeywords", Math.round(keywordScore * 10.0) / 10.0);
        res.breakdown = breakdown;

        return res;
    }

    private int calculateCandidateExperienceYears(List<Experience> experiences) {
        if (experiences.isEmpty()) return 0;

        int totalMonths = 0;
        for (Experience exp : experiences) {
            java.time.YearMonth start = parseYearMonth(exp.getStartDate());
            java.time.YearMonth end = (exp.isCurrentRole() || isPresent(exp.getEndDate()))
                    ? java.time.YearMonth.now()
                    : parseYearMonth(exp.getEndDate());

            if (start != null && end != null && !end.isBefore(start)) {
                long months = java.time.temporal.ChronoUnit.MONTHS.between(start, end) + 1;
                totalMonths += Math.max(1, months);
            } else {
                // Fallback for unparseable dates: estimate standard 18 months per role
                totalMonths += 18;
            }
        }

        return Math.max(1, (int) Math.round(totalMonths / 12.0));
    }

    private boolean isPresent(String dateStr) {
        return dateStr != null && (dateStr.equalsIgnoreCase("present")
                || dateStr.equalsIgnoreCase("current")
                || dateStr.equalsIgnoreCase("now"));
    }

    private java.time.YearMonth parseYearMonth(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        String cleaned = dateStr.trim();
        try {
            if (cleaned.length() >= 7 && cleaned.charAt(4) == '-') {
                int year = Integer.parseInt(cleaned.substring(0, 4));
                int month = Integer.parseInt(cleaned.substring(5, 7));
                if (month >= 1 && month <= 12) return java.time.YearMonth.of(year, month);
            } else if (cleaned.length() == 4) {
                int year = Integer.parseInt(cleaned);
                return java.time.YearMonth.of(year, 1);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private LearningRecommendationDto createRecommendation(SkillDictionary.SkillMeta meta, String importance) {
        return new LearningRecommendationDto(
                meta.getDisplayName(),
                meta.getCategory(),
                importance,
                meta.getEstimatedLearnTime(),
                meta.getKeyConcepts(),
                meta.getDocumentationUrl(),
                meta.getProjectIdeas()
        );
    }
}
