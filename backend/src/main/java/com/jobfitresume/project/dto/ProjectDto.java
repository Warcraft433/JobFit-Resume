package com.jobfitresume.project.dto;

import com.jobfitresume.project.Project;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ProjectDto {

    private UUID id;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;
    private String problemStatement;
    private String technicalStack;
    private String responsibilities;
    private String outcomes;
    private String githubUrl;
    private String demoUrl;
    private String startDate;
    private String endDate;
    private String category;
    private boolean featured;
    private int displayOrder;

    public ProjectDto() {
    }

    public static ProjectDto fromEntity(Project entity) {
        ProjectDto dto = new ProjectDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setProblemStatement(entity.getProblemStatement());
        dto.setTechnicalStack(entity.getTechnicalStack());
        dto.setResponsibilities(entity.getResponsibilities());
        dto.setOutcomes(entity.getOutcomes());
        dto.setGithubUrl(entity.getGithubUrl());
        dto.setDemoUrl(entity.getDemoUrl());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCategory(entity.getCategory());
        dto.setFeatured(entity.isFeatured());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }

    public void applyToEntity(Project entity) {
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setProblemStatement(this.problemStatement);
        entity.setTechnicalStack(this.technicalStack);
        entity.setResponsibilities(this.responsibilities);
        entity.setOutcomes(this.outcomes);
        entity.setGithubUrl(this.githubUrl);
        entity.setDemoUrl(this.demoUrl);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setCategory(this.category);
        entity.setFeatured(this.featured);
        entity.setDisplayOrder(this.displayOrder);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getTechnicalStack() {
        return technicalStack;
    }

    public void setTechnicalStack(String technicalStack) {
        this.technicalStack = technicalStack;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(String outcomes) {
        this.outcomes = outcomes;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
