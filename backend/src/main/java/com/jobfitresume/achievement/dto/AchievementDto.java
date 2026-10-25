package com.jobfitresume.achievement.dto;

import com.jobfitresume.achievement.Achievement;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class AchievementDto {

    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    private String issuer;
    private String achievementDate;
    private String description;
    private String url;

    public AchievementDto() {
    }

    public static AchievementDto fromEntity(Achievement entity) {
        AchievementDto dto = new AchievementDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setIssuer(entity.getIssuer());
        dto.setAchievementDate(entity.getAchievementDate());
        dto.setDescription(entity.getDescription());
        dto.setUrl(entity.getUrl());
        return dto;
    }

    public void applyToEntity(Achievement entity) {
        entity.setTitle(this.title);
        entity.setIssuer(this.issuer);
        entity.setAchievementDate(this.achievementDate);
        entity.setDescription(this.description);
        entity.setUrl(this.url);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAchievementDate() {
        return achievementDate;
    }

    public void setAchievementDate(String achievementDate) {
        this.achievementDate = achievementDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
