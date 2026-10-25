package com.jobfitresume.resume.dto;

import java.util.List;

public class GenerateResumeRequest {

    private String title;
    private String templateKey = "classic"; // classic, modern, technical
    private List<String> includedSections;
    private Integer maxProjects = 3;
    private Integer maxExperiences = 4;

    public GenerateResumeRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    public List<String> getIncludedSections() {
        return includedSections;
    }

    public void setIncludedSections(List<String> includedSections) {
        this.includedSections = includedSections;
    }

    public Integer getMaxProjects() {
        return maxProjects;
    }

    public void setMaxProjects(Integer maxProjects) {
        this.maxProjects = maxProjects;
    }

    public Integer getMaxExperiences() {
        return maxExperiences;
    }

    public void setMaxExperiences(Integer maxExperiences) {
        this.maxExperiences = maxExperiences;
    }
}
