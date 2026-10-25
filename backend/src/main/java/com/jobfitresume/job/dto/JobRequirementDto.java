package com.jobfitresume.job.dto;

import com.jobfitresume.job.JobRequirement;
import java.util.UUID;

public class JobRequirementDto {

    private UUID id;
    private String requirementText;
    private UUID canonicalSkillId;
    private String canonicalSkillName;
    private String requirementType;
    private String importance;
    private boolean matched;
    private String evidence;

    public JobRequirementDto() {
    }

    public static JobRequirementDto fromEntity(JobRequirement entity, String canonicalSkillName) {
        JobRequirementDto dto = new JobRequirementDto();
        dto.setId(entity.getId());
        dto.setRequirementText(entity.getRequirementText());
        dto.setCanonicalSkillId(entity.getCanonicalSkillId());
        dto.setCanonicalSkillName(canonicalSkillName);
        dto.setRequirementType(entity.getRequirementType());
        dto.setImportance(entity.getImportance());
        dto.setMatched(entity.isMatched());
        dto.setEvidence(entity.getEvidenceJson());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getCanonicalSkillName() {
        return canonicalSkillName;
    }

    public void setCanonicalSkillName(String canonicalSkillName) {
        this.canonicalSkillName = canonicalSkillName;
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

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}
