package com.jobfitresume.resume.dto;

import com.jobfitresume.resume.ResumeVersion;
import java.time.Instant;
import java.util.UUID;

public class ResumeVersionDto {

    private UUID id;
    private UUID resumeId;
    private Integer versionNumber;
    private String contentJson;
    private ResumeContentModel content;
    private Instant createdAt;

    public ResumeVersionDto() {
    }

    public static ResumeVersionDto fromEntity(ResumeVersion entity) {
        ResumeVersionDto dto = new ResumeVersionDto();
        dto.setId(entity.getId());
        dto.setResumeId(entity.getResumeId());
        dto.setVersionNumber(entity.getVersionNumber());
        dto.setContentJson(entity.getContentJson());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getResumeId() {
        return resumeId;
    }

    public void setResumeId(UUID resumeId) {
        this.resumeId = resumeId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public ResumeContentModel getContent() {
        return content;
    }

    public void setContent(ResumeContentModel content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
