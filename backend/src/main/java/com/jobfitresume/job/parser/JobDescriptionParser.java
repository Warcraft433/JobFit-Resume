package com.jobfitresume.job.parser;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobDescriptionParser {

    public static class ParsedJobData {
        private final Set<SkillDictionary.SkillMeta> requiredSkills = new LinkedHashSet<>();
        private final Set<SkillDictionary.SkillMeta> preferredSkills = new LinkedHashSet<>();
        private final int minExperienceYears;
        private final String degreeRequirement;
        private final List<String> responsibilities = new ArrayList<>();
        private final List<String> extractedRequirements = new ArrayList<>();

        public ParsedJobData(
                Set<SkillDictionary.SkillMeta> requiredSkills,
                Set<SkillDictionary.SkillMeta> preferredSkills,
                int minExperienceYears,
                String degreeRequirement,
                List<String> responsibilities,
                List<String> extractedRequirements) {
            this.requiredSkills.addAll(requiredSkills);
            this.preferredSkills.addAll(preferredSkills);
            this.minExperienceYears = minExperienceYears;
            this.degreeRequirement = degreeRequirement;
            this.responsibilities.addAll(responsibilities);
            this.extractedRequirements.addAll(extractedRequirements);
        }

        public Set<SkillDictionary.SkillMeta> getRequiredSkills() {
            return requiredSkills;
        }

        public Set<SkillDictionary.SkillMeta> getPreferredSkills() {
            return preferredSkills;
        }

        public int getMinExperienceYears() {
            return minExperienceYears;
        }

        public String getDegreeRequirement() {
            return degreeRequirement;
        }

        public List<String> getResponsibilities() {
            return responsibilities;
        }

        public List<String> getExtractedRequirements() {
            return extractedRequirements;
        }
    }

    private final SkillDictionary skillDictionary;

    private static final Pattern EXPERIENCE_PATTERN = Pattern.compile(
            "(?:(\\d+)\\s*(?:\\+|plus)?|(\\d+)\\s*(?:-|to)\\s*(\\d+))\\s*(?:years?|yrs?)(?:\\s*of)?\\s*(?:experience|exp)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DEGREE_PATTERN = Pattern.compile(
            "\\b(ph\\.?d|doctorate|master'?s|m\\.?s\\.?|mba|bachelor'?s|b\\.?s\\.?|b\\.?e\\.?|b\\.?tech|associate'?s|computer science|engineering|stem)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public JobDescriptionParser(SkillDictionary skillDictionary) {
        this.skillDictionary = skillDictionary;
    }

    public ParsedJobData parse(String jobDescription) {
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return new ParsedJobData(Collections.emptySet(), Collections.emptySet(), 0, "Not Specified", Collections.emptyList(), Collections.emptyList());
        }

        String[] sections = splitSections(jobDescription);
        String requiredSection = sections[0];
        String preferredSection = sections[1];
        String responsibilitiesSection = sections[2];

        // 1. Extract skills
        Set<SkillDictionary.SkillMeta> prefSkills = skillDictionary.extractSkillsFromText(preferredSection);
        Set<SkillDictionary.SkillMeta> reqSkills = skillDictionary.extractSkillsFromText(requiredSection);

        // Include any skills mentioned across the full job description into required skills unless specifically preferred
        Set<SkillDictionary.SkillMeta> allSkills = skillDictionary.extractSkillsFromText(jobDescription);
        for (SkillDictionary.SkillMeta s : allSkills) {
            if (!prefSkills.contains(s)) {
                reqSkills.add(s);
            }
        }

        // 2. Extract Experience Years
        int expYears = extractExperienceYears(jobDescription);

        // 3. Extract Degree Requirement
        String degree = extractDegree(jobDescription);

        // 4. Extract Responsibilities & Bullet points
        List<String> responsibilities = extractBulletPoints(responsibilitiesSection.isEmpty() ? jobDescription : responsibilitiesSection);
        if (responsibilities.isEmpty()) {
            responsibilities = extractBulletPoints(jobDescription);
        }

        // Limit responsibilities to top 6 clear items
        if (responsibilities.size() > 6) {
            responsibilities = responsibilities.subList(0, 6);
        }

        List<String> rawReqs = extractBulletPoints(requiredSection.isEmpty() ? jobDescription : requiredSection);

        return new ParsedJobData(reqSkills, prefSkills, expYears, degree, responsibilities, rawReqs);
    }

    private int extractExperienceYears(String text) {
        Matcher matcher = EXPERIENCE_PATTERN.matcher(text);
        int maxFound = 0;
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                int val = Integer.parseInt(matcher.group(1));
                maxFound = Math.max(maxFound, val);
            } else if (matcher.group(2) != null) {
                int val = Integer.parseInt(matcher.group(2));
                maxFound = Math.max(maxFound, val);
            }
        }
        return Math.min(maxFound, 20); // sanity cap
    }

    private String extractDegree(String text) {
        Matcher matcher = DEGREE_PATTERN.matcher(text);
        if (matcher.find()) {
            String match = matcher.group(1).toLowerCase();
            if (match.contains("ph") || match.contains("doc")) return "PhD / Doctorate";
            if (match.contains("master") || match.contains("m.s") || match.contains("mba")) return "Master's Degree";
            if (match.contains("bachelor") || match.contains("b.s") || match.contains("b.tech") || match.contains("b.e")) return "Bachelor's Degree";
            if (match.contains("associate")) return "Associate Degree";
            return "Degree in Computer Science or Related Field";
        }
        return "Bachelor's Degree preferred or equivalent experience";
    }

    private String[] splitSections(String text) {
        String lower = text.toLowerCase();
        int reqIndex = findFirstIndex(lower, "requirements", "qualifications", "what you need", "what we're looking for", "must have");
        int prefIndex = findFirstIndex(lower, "preferred", "nice to have", "bonus points", "plus", "desired");
        int respIndex = findFirstIndex(lower, "responsibilities", "what you will do", "what you'll do", "the role", "duties");

        String required = text;
        String preferred = "";
        String responsibilities = "";

        if (prefIndex > -1 && reqIndex > -1 && prefIndex > reqIndex) {
            required = text.substring(reqIndex, prefIndex);
            preferred = text.substring(prefIndex);
        } else if (reqIndex > -1) {
            required = text.substring(reqIndex);
        }

        if (respIndex > -1) {
            int endIndex = reqIndex > respIndex ? reqIndex : (prefIndex > respIndex ? prefIndex : text.length());
            responsibilities = text.substring(respIndex, endIndex);
        }

        return new String[]{required, preferred, responsibilities};
    }

    private int findFirstIndex(String text, String... keywords) {
        int earliest = -1;
        for (String kw : keywords) {
            int idx = text.indexOf(kw);
            if (idx != -1 && (earliest == -1 || idx < earliest)) {
                earliest = idx;
            }
        }
        return earliest;
    }

    private List<String> extractBulletPoints(String text) {
        List<String> bullets = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("•") || trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.matches("^\\d+\\..*")) {
                String clean = trimmed.replaceAll("^[•\\-*\\d.]+\\s*", "").trim();
                if (clean.length() >= 15 && clean.length() <= 280) {
                    bullets.add(clean);
                }
            }
        }
        return bullets;
    }
}
