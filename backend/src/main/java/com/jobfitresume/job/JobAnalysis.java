package com.jobfitresume.job;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_analyses")
public class JobAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "parser_version", nullable = false)
    private String parserVersion = "1.0.0";

    @Column(name = "analysis_json", nullable = false, columnDefinition = "TEXT")
    private String analysisJson;

    @Column(name = "match_score", nullable = false)
    private Double matchScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public JobAnalysis() {
    }

    public JobAnalysis(UUID userId, UUID jobId, String parserVersion, String analysisJson, Double matchScore) {
        this.userId = userId;
        this.jobId = jobId;
        this.parserVersion = parserVersion;
        this.analysisJson = analysisJson;
        this.matchScore = matchScore;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getParserVersion() {
        return parserVersion;
    }

    public void setParserVersion(String parserVersion) {
        this.parserVersion = parserVersion;
    }

    public String getAnalysisJson() {
        return analysisJson;
    }

    public void setAnalysisJson(String analysisJson) {
        this.analysisJson = analysisJson;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
