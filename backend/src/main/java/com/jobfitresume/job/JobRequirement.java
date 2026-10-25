package com.jobfitresume.job;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "job_requirements")
public class JobRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_analysis_id", nullable = false)
    private UUID jobAnalysisId;

    @Column(name = "requirement_text", nullable = false, columnDefinition = "TEXT")
    private String requirementText;

    @Column(name = "canonical_skill_id")
    private UUID canonicalSkillId;

    @Column(name = "requirement_type", nullable = false)
    private String requirementType;

    @Column(name = "importance", nullable = false)
    private String importance;

    @Column(name = "matched", nullable = false)
    private boolean matched = false;

    @Column(name = "evidence_json", columnDefinition = "TEXT")
    private String evidenceJson;

    public JobRequirement() {
    }

    public JobRequirement(UUID jobAnalysisId, String requirementText, String requirementType, String importance) {
        this.jobAnalysisId = jobAnalysisId;
        this.requirementText = requirementText;
        this.requirementType = requirementType;
        this.importance = importance;
        this.matched = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getJobAnalysisId() {
        return jobAnalysisId;
    }

    public void setJobAnalysisId(UUID jobAnalysisId) {
        this.jobAnalysisId = jobAnalysisId;
    }

    public String getRequirementText() {
        return requirementText;
    }

    public void setRequirementText(String requirementText) {
        this.requirementText = requirementText;
    }

    public UUID getCanonicalSkillId() {
        return canonicalSkillId;
    }

    public void setCanonicalSkillId(UUID canonicalSkillId) {
        this.canonicalSkillId = canonicalSkillId;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getEvidenceJson() {
        return evidenceJson;
    }

    public void setEvidenceJson(String evidenceJson) {
        this.evidenceJson = evidenceJson;
    }
}
