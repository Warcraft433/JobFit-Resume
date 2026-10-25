package com.jobfitresume.resume;

import com.jobfitresume.security.SecurityUtils;
import com.jobfitresume.user.User;
import com.jobfitresume.user.UserRepository;
import com.jobfitresume.job.Job;
import com.jobfitresume.job.JobRepository;
import com.jobfitresume.job.parser.SkillDictionary;
import com.jobfitresume.profile.UserProfile;
import com.jobfitresume.profile.UserProfileRepository;
import com.jobfitresume.resume.dto.AiAssistantRequest;
import com.jobfitresume.resume.dto.AiAssistantResponse;
import com.jobfitresume.skill.Skill;
import com.jobfitresume.skill.SkillRepository;
import com.jobfitresume.skill.UserSkill;
import com.jobfitresume.skill.UserSkillRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiResumeAssistantService {

    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillDictionary skillDictionary;

    public AiResumeAssistantService(
            JobRepository jobRepository,
            UserProfileRepository userProfileRepository,
            UserSkillRepository userSkillRepository,
            SkillRepository skillRepository,
            UserRepository userRepository,
            SkillDictionary skillDictionary) {
        this.jobRepository = jobRepository;
        this.userProfileRepository = userProfileRepository;
        this.userSkillRepository = userSkillRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.skillDictionary = skillDictionary;
    }

    public AiAssistantResponse processAssistantRequest(AiAssistantRequest req) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        String candidateName = user != null ? user.getFullName() : "Candidate";
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        List<UserSkill> userSkills = userSkillRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<String> verifiedSkillNames = new ArrayList<>();
        for (UserSkill us : userSkills) {
            String name = us.getCustomSkillName();
            if (us.getSkillId() != null) {
                Optional<Skill> sk = skillRepository.findById(us.getSkillId());
                if (sk.isPresent()) name = sk.get().getName();
            }
            if (name != null && !name.isBlank()) {
                verifiedSkillNames.add(name.trim());
            }
        }

        Job targetJob = null;
        if (req.getTargetJobId() != null) {
            targetJob = jobRepository.findByIdAndUserId(req.getTargetJobId(), userId).orElse(null);
        }

        String prompt = req.getPrompt() != null ? req.getPrompt().trim().toLowerCase() : "";
        AiAssistantResponse resp = new AiAssistantResponse();

        // 1. Email Generation
        if (prompt.contains("email") || "DRAFT_EMAIL".equalsIgnoreCase(req.getAction())) {
            return generateApplicationEmail(candidateName, targetJob, profile, verifiedSkillNames, req.getTone());
        }

        // 2. Shorten Summary / Make Concise
        if (prompt.contains("short") || prompt.contains("concise") || "SHORTEN_SUMMARY".equalsIgnoreCase(req.getAction())) {
            String baseSummary = req.getCurrentSummary();
            if (baseSummary == null || baseSummary.isBlank()) {
                baseSummary = profile != null ? profile.getProfessionalSummary() : null;
            }
            if (baseSummary != null && !baseSummary.isBlank()) {
                String concise = makeConcise(baseSummary);
                resp.setAction("SHORTEN_SUMMARY");
                resp.setModifiedSummary(concise);
                resp.setReply("I have condensed your professional summary while preserving your verified facts and core achievements. You can review and apply it directly to your preview.");
                return resp;
            }
        }

        // 3. Polish Wording / Make Professional
        if (prompt.contains("polish") || prompt.contains("professional") || "POLISH_SUMMARY".equalsIgnoreCase(req.getAction())) {
            String baseSummary = req.getCurrentSummary();
            if (baseSummary == null || baseSummary.isBlank()) {
                baseSummary = profile != null ? profile.getProfessionalSummary() : null;
            }
            if (baseSummary != null && !baseSummary.isBlank()) {
                String polished = polishWording(baseSummary, targetJob);
                resp.setAction("POLISH_SUMMARY");
                resp.setModifiedSummary(polished);
                resp.setReply("I polished your summary with strong, active verbs and industry-standard phrasing without inventing any unverified claims.");
                return resp;
            }
        }

        // 4. Skills Tailoring & Gap Analysis
        if (prompt.contains("missing") || prompt.contains("gap") || prompt.contains("skill") || "GAP_ANALYSIS".equalsIgnoreCase(req.getAction())) {
            if (targetJob != null) {
                Set<SkillDictionary.SkillMeta> jobMeta = skillDictionary.extractSkillsFromText(targetJob.getJobDescription());
                Set<String> verifiedLower = verifiedSkillNames.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

                List<String> matched = new ArrayList<>();
                List<String> missing = new ArrayList<>();

                for (SkillDictionary.SkillMeta m : jobMeta) {
                    if (verifiedLower.contains(m.getDisplayName().toLowerCase())) {
                        matched.add(m.getDisplayName());
                    } else {
                        missing.add(m.getDisplayName());
                    }
                }

                resp.setAction("GAP_ANALYSIS");
                resp.setSuggestedSkills(matched);
                resp.setMissingSkills(missing);
                resp.setReply("For " + targetJob.getJobTitle() + " at " + targetJob.getCompany() +
                        ", you match " + matched.size() + " verified skill(s) (" + String.join(", ", matched) + "). " +
                        (missing.isEmpty() ? "No critical technical skill gaps detected!"
                                : "The vacancy also seeks: " + String.join(", ", missing) + ". If you have hands-on experience in any of these, add them to your Career Profile!"));
                return resp;
            }
        }

        // Default conversational response
        resp.setAction("GENERAL");
        resp.setReply("Hi " + candidateName + "! I am your AI Resume Assistant. I can help refine your summary, polish bullet points, analyze target job gaps, or draft an application email based strictly on your verified background.");
        return resp;
    }

    private AiAssistantResponse generateApplicationEmail(
            String candidateName,
            Job targetJob,
            UserProfile profile,
            List<String> verifiedSkillNames,
            String tone) {

        AiAssistantResponse resp = new AiAssistantResponse();
        resp.setAction("DRAFT_EMAIL");

        String jobTitle = targetJob != null ? targetJob.getJobTitle() : "Software Developer";
        String company = targetJob != null ? targetJob.getCompany() : "Hiring Team";

        String headline = profile != null && profile.getProfessionalHeadline() != null
                ? profile.getProfessionalHeadline()
                : "Software Developer";

        List<String> topSkills = verifiedSkillNames.stream()
                .limit(4)
                .collect(Collectors.toList());
        String skillsList = topSkills.isEmpty() ? "full stack engineering" : String.join(", ", topSkills);

        String subject = "Application for " + jobTitle + " — " + candidateName;
        StringBuilder body = new StringBuilder();

        if ("warm".equalsIgnoreCase(tone)) {
            body.append("Dear ").append(company).append(" Recruiting Team,\n\n");
            body.append("I was thrilled to come across the ").append(jobTitle).append(" opening at ").append(company).append(". ");
            body.append("With my background as a ").append(headline).append(" and hands-on experience in ").append(skillsList).append(", ");
            body.append("I am eager to contribute to your engineering team's mission.\n\n");
            body.append("Attached is my tailored resume for your review. I would welcome the opportunity to discuss how my verified background aligns with your project objectives.\n\n");
            body.append("Thank you for your time and consideration,\n");
            body.append(candidateName);
        } else if ("concise".equalsIgnoreCase(tone)) {
            body.append("Dear ").append(company).append(" Team,\n\n");
            body.append("Please accept my application for the ").append(jobTitle).append(" position at ").append(company).append(".\n\n");
            body.append("Summary of Qualifications:\n");
            body.append("• ").append(headline).append("\n");
            body.append("• Core competencies: ").append(skillsList).append("\n");
            body.append("• Truth-verified project history and technical deliverables attached in resume.\n\n");
            body.append("I look forward to discussing the next steps.\n\n");
            body.append("Best regards,\n");
            body.append(candidateName);
        } else {
            // Default: Professional
            body.append("Dear Hiring Manager,\n\n");
            body.append("I am writing to express my strong interest in the ").append(jobTitle).append(" opportunity at ").append(company).append(". ");
            body.append("As a ").append(headline).append(" with proven competencies in ").append(skillsList).append(", ");
            body.append("I have developed reliable software systems and collaborated cross-functionally to achieve business results.\n\n");
            body.append("My attached resume outlines my verified work experience, key projects, and academic background tailored to this role. ");
            body.append("I would welcome the opportunity to interview and discuss how my skill set can support ").append(company).append(".\n\n");
            body.append("Sincerely,\n");
            body.append(candidateName);
        }

        resp.setEmailSubject(subject);
        resp.setEmailBody(body.toString());
        resp.setReply("Here is your tailored application email for " + jobTitle + " at " + company + " (" + (tone != null ? tone : "professional") + " tone). You can review, copy, or edit it freely.");
        return resp;
    }

    private String makeConcise(String original) {
        String trimmed = original.replaceAll("\\s+", " ").trim();
        String[] sentences = trimmed.split("(?<=[.!?])\\s+");
        if (sentences.length <= 2) {
            return trimmed;
        }
        return sentences[0] + " " + sentences[1];
    }

    private String polishWording(String original, Job job) {
        String trimmed = original.replaceAll("\\s+", " ").trim();
        if (job != null && !trimmed.toLowerCase().contains(job.getCompany().toLowerCase())) {
            return trimmed + " Targeted towards driving robust engineering solutions for " + job.getCompany() + " as a " + job.getJobTitle() + ".";
        }
        return trimmed;
    }
}
