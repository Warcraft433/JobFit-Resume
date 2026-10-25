package com.jobfitresume.resume.dto;

import com.jobfitresume.resume.Resume;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public class ResumeDto {

    private UUID id;
    private UUID jobId;

    @NotBlank(message = "Resume title is required")
    private String title;

    private String templateKey = "classic";
    private Integer latestVersionNumber;
    private Instant createdAt;
    private Instant updatedAt;

    public ResumeDto() {
    }

    public static ResumeDto fromEntity(Resume entity) {
        ResumeDto dto = new ResumeDto();
        dto.setId(entity.getId());
        dto.setJobId(entity.getJobId());
        dto.setTitle(entity.getTitle());
        dto.setTemplateKey(entity.getTemplateKey());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
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

    public Integer getLatestVersionNumber() {
        return latestVersionNumber;
    }

    public void setLatestVersionNumber(Integer latestVersionNumber) {
        this.latestVersionNumber = latestVersionNumber;
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
