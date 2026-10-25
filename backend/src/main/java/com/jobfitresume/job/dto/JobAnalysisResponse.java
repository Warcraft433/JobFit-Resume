package com.jobfitresume.job.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobAnalysisResponse {

    private UUID analysisId;
    private UUID jobId;
    private double matchScore; // 0 to 100
    private Map<String, Double> scoreBreakdown; // skills: 50, experience: 25, education: 15, keywords: 10
    private List<JobRequirementDto> requirements;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String experienceAssessment;
    private String educationAssessment;
    private List<String> keyResponsibilities;
    private Instant analyzedAt;

    public JobAnalysisResponse() {
    }

    public UUID getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(UUID analysisId) {
        this.analysisId = analysisId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public Map<String, Double> getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(Map<String, Double> scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public List<JobRequirementDto> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<JobRequirementDto> requirements) {
        this.requirements = requirements;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getExperienceAssessment() {
        return experienceAssessment;
    }

    public void setExperienceAssessment(String experienceAssessment) {
        this.experienceAssessment = experienceAssessment;
    }

    public String getEducationAssessment() {
        return educationAssessment;
    }

    public void setEducationAssessment(String educationAssessment) {
        this.educationAssessment = educationAssessment;
    }

    public List<String> getKeyResponsibilities() {
        return keyResponsibilities;
    }

    public void setKeyResponsibilities(List<String> keyResponsibilities) {
        this.keyResponsibilities = keyResponsibilities;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(Instant analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
