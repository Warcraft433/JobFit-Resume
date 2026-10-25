package com.jobfitresume.experience.dto;

import com.jobfitresume.experience.Experience;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ExperienceDto {

    private UUID id;

    @NotBlank(message = "Employer is required")
    private String employer;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String location;
    private String startDate;
    private String endDate;
    private boolean currentRole;
    private String description;
    private String achievements;
    private String technologies;
    private int displayOrder;

    public ExperienceDto() {
    }

    public static ExperienceDto fromEntity(Experience entity) {
        ExperienceDto dto = new ExperienceDto();
        dto.setId(entity.getId());
        dto.setEmployer(entity.getEmployer());
        dto.setJobTitle(entity.getJobTitle());
        dto.setLocation(entity.getLocation());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCurrentRole(entity.isCurrentRole());
        dto.setDescription(entity.getDescription());
        dto.setAchievements(entity.getAchievements());
        dto.setTechnologies(entity.getTechnologies());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }

    public void applyToEntity(Experience entity) {
        entity.setEmployer(this.employer);
        entity.setJobTitle(this.jobTitle);
        entity.setLocation(this.location);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setCurrentRole(this.currentRole);
        entity.setDescription(this.description);
        entity.setAchievements(this.achievements);
        entity.setTechnologies(this.technologies);
        entity.setDisplayOrder(this.displayOrder);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(boolean currentRole) {
        this.currentRole = currentRole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
