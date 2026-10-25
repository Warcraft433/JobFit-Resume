package com.jobfitresume.application.dto;

import com.jobfitresume.application.Application;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class ApplicationDto {

    private UUID id;

    @NotNull(message = "Job ID is required")
    private UUID jobId;

    private String jobTitle;
    private String company;
    private String location;

    private UUID resumeVersionId;
    private Integer resumeVersionNumber;

    private String status = "SAVED";
    private Instant appliedAt;
    private Instant followUpAt;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public ApplicationDto() {
    }

    public static ApplicationDto fromEntity(Application entity) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(entity.getId());
        dto.setJobId(entity.getJobId());
        dto.setResumeVersionId(entity.getResumeVersionId());
        dto.setStatus(entity.getStatus());
        dto.setAppliedAt(entity.getAppliedAt());
        dto.setFollowUpAt(entity.getFollowUpAt());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public void applyToEntity(Application entity) {
        entity.setJobId(this.jobId);
        entity.setResumeVersionId(this.resumeVersionId);
        if (this.status != null) entity.setStatus(this.status);
        entity.setAppliedAt(this.appliedAt);
        entity.setFollowUpAt(this.followUpAt);
        entity.setNotes(this.notes);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UUID getResumeVersionId() {
        return resumeVersionId;
    }

    public void setResumeVersionId(UUID resumeVersionId) {
        this.resumeVersionId = resumeVersionId;
    }

    public Integer getResumeVersionNumber() {
        return resumeVersionNumber;
    }

    public void setResumeVersionNumber(Integer resumeVersionNumber) {
        this.resumeVersionNumber = resumeVersionNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Instant getFollowUpAt() {
        return followUpAt;
    }

    public void setFollowUpAt(Instant followUpAt) {
        this.followUpAt = followUpAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
