package com.jobfitresume.profile.dto;

import com.jobfitresume.profile.UserProfile;

public class UserProfileDto {

    private String professionalHeadline;
    private String contactEmail;
    private String phone;
    private String city;
    private String country;
    private String professionalSummary;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String customLinks;

    public UserProfileDto() {
    }

    public static UserProfileDto fromEntity(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setProfessionalHeadline(profile.getProfessionalHeadline());
        dto.setContactEmail(profile.getContactEmail());
        dto.setPhone(profile.getPhone());
        dto.setCity(profile.getCity());
        dto.setCountry(profile.getCountry());
        dto.setProfessionalSummary(profile.getProfessionalSummary());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setGithubUrl(profile.getGithubUrl());
        dto.setPortfolioUrl(profile.getPortfolioUrl());
        dto.setCustomLinks(profile.getCustomLinks());
        return dto;
    }

    public void updateEntity(UserProfile profile) {
        profile.setProfessionalHeadline(this.professionalHeadline);
        profile.setContactEmail(this.contactEmail);
        profile.setPhone(this.phone);
        profile.setCity(this.city);
        profile.setCountry(this.country);
        profile.setProfessionalSummary(this.professionalSummary);
        profile.setLinkedinUrl(this.linkedinUrl);
        profile.setGithubUrl(this.githubUrl);
        profile.setPortfolioUrl(this.portfolioUrl);
        profile.setCustomLinks(this.customLinks);
    }

    public String getProfessionalHeadline() {
        return professionalHeadline;
    }

    public void setProfessionalHeadline(String professionalHeadline) {
        this.professionalHeadline = professionalHeadline;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getCustomLinks() {
        return customLinks;
    }

    public void setCustomLinks(String customLinks) {
        this.customLinks = customLinks;
    }
}
