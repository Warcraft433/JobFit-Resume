package com.jobfitresume.certification.dto;

import com.jobfitresume.certification.Certification;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class CertificationDto {

    private UUID id;

    @NotBlank(message = "Certification name is required")
    private String name;

    @NotBlank(message = "Issuer is required")
    private String issuer;

    private String issueDate;
    private String expirationDate;
    private String credentialId;
    private String credentialUrl;
    private String description;

    public CertificationDto() {
    }

    public static CertificationDto fromEntity(Certification entity) {
        CertificationDto dto = new CertificationDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIssuer(entity.getIssuer());
        dto.setIssueDate(entity.getIssueDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setCredentialId(entity.getCredentialId());
        dto.setCredentialUrl(entity.getCredentialUrl());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public void applyToEntity(Certification entity) {
        entity.setName(this.name);
        entity.setIssuer(this.issuer);
        entity.setIssueDate(this.issueDate);
        entity.setExpirationDate(this.expirationDate);
        entity.setCredentialId(this.credentialId);
        entity.setCredentialUrl(this.credentialUrl);
        entity.setDescription(this.description);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
