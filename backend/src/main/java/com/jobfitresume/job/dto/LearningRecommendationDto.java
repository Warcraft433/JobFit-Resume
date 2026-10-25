package com.jobfitresume.job.dto;

import java.util.List;

public class LearningRecommendationDto {

    private String skillName;
    private String category;
    private String importance;
    private String estimatedTimeToLearn;
    private String keyConcepts;
    private String documentationUrl;
    private List<String> practiceProjectIdeas;

    public LearningRecommendationDto() {
    }

    public LearningRecommendationDto(
            String skillName,
            String category,
            String importance,
            String estimatedTimeToLearn,
            String keyConcepts,
            String documentationUrl,
            List<String> practiceProjectIdeas) {
        this.skillName = skillName;
        this.category = category;
        this.importance = importance;
        this.estimatedTimeToLearn = estimatedTimeToLearn;
        this.keyConcepts = keyConcepts;
        this.documentationUrl = documentationUrl;
        this.practiceProjectIdeas = practiceProjectIdeas;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getEstimatedTimeToLearn() {
        return estimatedTimeToLearn;
    }

    public void setEstimatedTimeToLearn(String estimatedTimeToLearn) {
        this.estimatedTimeToLearn = estimatedTimeToLearn;
    }

    public String getKeyConcepts() {
        return keyConcepts;
    }

    public void setKeyConcepts(String keyConcepts) {
        this.keyConcepts = keyConcepts;
    }

    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public List<String> getPracticeProjectIdeas() {
        return practiceProjectIdeas;
    }

    public void setPracticeProjectIdeas(List<String> practiceProjectIdeas) {
        this.practiceProjectIdeas = practiceProjectIdeas;
    }
}
