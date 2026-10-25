package com.jobfitresume.job.dto;

import java.util.List;
import java.util.UUID;

public class SkillsGapResponse {

    private UUID jobId;
    private double overallMatchScore;
    private int totalSkillsRequired;
    private int matchedSkillsCount;
    private int missingSkillsCount;
    private List<String> matchedSkills;
    private List<String> missingRequiredSkills;
    private List<String> missingPreferredSkills;
    private List<LearningRecommendationDto> recommendations;

    public SkillsGapResponse() {
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public double getOverallMatchScore() {
        return overallMatchScore;
    }

    public void setOverallMatchScore(double overallMatchScore) {
        this.overallMatchScore = overallMatchScore;
    }

    public int getTotalSkillsRequired() {
        return totalSkillsRequired;
    }

    public void setTotalSkillsRequired(int totalSkillsRequired) {
        this.totalSkillsRequired = totalSkillsRequired;
    }

    public int getMatchedSkillsCount() {
        return matchedSkillsCount;
    }

    public void setMatchedSkillsCount(int matchedSkillsCount) {
        this.matchedSkillsCount = matchedSkillsCount;
    }

    public int getMissingSkillsCount() {
        return missingSkillsCount;
    }

    public void setMissingSkillsCount(int missingSkillsCount) {
        this.missingSkillsCount = missingSkillsCount;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingRequiredSkills() {
        return missingRequiredSkills;
    }

    public void setMissingRequiredSkills(List<String> missingRequiredSkills) {
        this.missingRequiredSkills = missingRequiredSkills;
    }

    public List<String> getMissingPreferredSkills() {
        return missingPreferredSkills;
    }

    public void setMissingPreferredSkills(List<String> missingPreferredSkills) {
        this.missingPreferredSkills = missingPreferredSkills;
    }

    public List<LearningRecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<LearningRecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }
}
