package com.jobfitresume.skill.dto;

import com.jobfitresume.skill.UserSkill;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class UserSkillDto {

    private UUID id;
    private UUID skillId;

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private String canonicalName;

    @NotBlank(message = "Category is required")
    private String category;

    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    private Integer yearsExperience;
    private String notes;

    public UserSkillDto() {
    }

    public static UserSkillDto fromEntity(UserSkill entity) {
        UserSkillDto dto = new UserSkillDto();
        dto.setId(entity.getId());
        dto.setSkillId(entity.getSkillId());
        dto.setSkillName(entity.getCustomSkillName());
        dto.setCategory(entity.getCategory());
        dto.setProficiency(entity.getProficiency());
        dto.setYearsExperience(entity.getYearsExperience());
        dto.setNotes(entity.getNotes());
        return dto;
    }

    public void applyToEntity(UserSkill entity) {
        entity.setSkillId(this.skillId);
        entity.setCustomSkillName(this.skillName);
        entity.setCategory(this.category);
        entity.setProficiency(this.proficiency);
        entity.setYearsExperience(this.yearsExperience);
        entity.setNotes(this.notes);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public void setSkillId(UUID skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
