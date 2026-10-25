package com.jobfitresume.resume;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resume_versions")
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "resume_id", nullable = false)
    private UUID resumeId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "content_json", nullable = false, columnDefinition = "TEXT")
    private String contentJson;

    @Column(name = "profile_snapshot_json", columnDefinition = "TEXT")
    private String profileSnapshotJson;

    @Column(name = "analysis_snapshot_json", columnDefinition = "TEXT")
    private String analysisSnapshotJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ResumeVersion() {
    }

    public ResumeVersion(UUID resumeId, Integer versionNumber, String contentJson, String profileSnapshotJson, String analysisSnapshotJson) {
        this.resumeId = resumeId;
        this.versionNumber = versionNumber;
        this.contentJson = contentJson;
        this.profileSnapshotJson = profileSnapshotJson;
        this.analysisSnapshotJson = analysisSnapshotJson;
        this.createdAt = Instant.now();
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

    public String getProfileSnapshotJson() {
        return profileSnapshotJson;
    }

    public void setProfileSnapshotJson(String profileSnapshotJson) {
        this.profileSnapshotJson = profileSnapshotJson;
    }

    public String getAnalysisSnapshotJson() {
        return analysisSnapshotJson;
    }

    public void setAnalysisSnapshotJson(String analysisSnapshotJson) {
        this.analysisSnapshotJson = analysisSnapshotJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
