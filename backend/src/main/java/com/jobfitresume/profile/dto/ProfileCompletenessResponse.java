package com.jobfitresume.profile.dto;

import java.util.List;
import java.util.Map;

public class ProfileCompletenessResponse {

    private int score;
    private Map<String, Boolean> sections;
    private List<String> recommendations;

    public ProfileCompletenessResponse() {
    }

    public ProfileCompletenessResponse(int score, Map<String, Boolean> sections, List<String> recommendations) {
        this.score = score;
        this.sections = sections;
        this.recommendations = recommendations;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Boolean> getSections() {
        return sections;
    }

    public void setSections(Map<String, Boolean> sections) {
        this.sections = sections;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
