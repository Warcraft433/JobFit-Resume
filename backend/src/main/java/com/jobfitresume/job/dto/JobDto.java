package com.jobfitresume.job.dto;

import com.jobfitresume.job.Job;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public class JobDto {

    private UUID id;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    private String location;

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    private String jobUrl;
    private String salaryText;
    private Instant savedAt;
    private Double latestMatchScore;

    public JobDto() {
    }

    public static JobDto fromEntity(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setCompany(job.getCompany());
        dto.setJobTitle(job.getJobTitle());
        dto.setLocation(job.getLocation());
        dto.setJobDescription(job.getJobDescription());
        dto.setJobUrl(job.getJobUrl());
        dto.setSalaryText(job.getSalaryText());
        dto.setSavedAt(job.getSavedAt());
        return dto;
    }

    public void applyToEntity(Job job) {
        job.setCompany(this.company);
        job.setJobTitle(this.jobTitle);
        job.setLocation(this.location);
        job.setJobDescription(this.jobDescription);
        job.setJobUrl(this.jobUrl);
        job.setSalaryText(this.salaryText);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getSalaryText() {
        return salaryText;
    }

    public void setSalaryText(String salaryText) {
        this.salaryText = salaryText;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Instant savedAt) {
        this.savedAt = savedAt;
    }

    public Double getLatestMatchScore() {
        return latestMatchScore;
    }

    public void setLatestMatchScore(Double latestMatchScore) {
        this.latestMatchScore = latestMatchScore;
    }
}
