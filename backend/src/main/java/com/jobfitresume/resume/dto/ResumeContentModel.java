package com.jobfitresume.resume.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeContentModel {

    public static class ContactInfo {
        public String fullName = "";
        public String headline = "";
        public String email = "";
        public String phone = "";
        public String location = "";
        public String linkedinUrl = "";
        public String githubUrl = "";
        public String portfolioUrl = "";
        public String customLinks = "";
    }

    public static class ExperienceItem {
        public String employer = "";
        public String jobTitle = "";
        public String location = "";
        public String startDate = "";
        public String endDate = "";
        public boolean currentRole = false;
        public String description = "";
        public List<String> bulletPoints = new ArrayList<>();
        public List<String> technologies = new ArrayList<>();
    }

    public static class EducationItem {
        public String institution = "";
        public String degree = "";
        public String fieldOfStudy = "";
        public String startDate = "";
        public String endDate = "";
        public boolean currentlyStudying = false;
        public String grade = "";
    }

    public static class ProjectItem {
        public String name = "";
        public String category = "";
        public String description = "";
        public String technicalStack = "";
        public String responsibilities = "";
        public String outcomes = "";
        public String githubUrl = "";
        public String demoUrl = "";
    }

    public static class CertificationItem {
        public String name = "";
        public String issuer = "";
        public String issueDate = "";
        public String credentialUrl = "";
    }

    public static class AchievementItem {
        public String title = "";
        public String issuer = "";
        public String achievementDate = "";
        public String description = "";
    }

    private ContactInfo contact = new ContactInfo();
    private String professionalSummary = "";
    private List<String> primarySkills = new ArrayList<>();
    private List<String> secondarySkills = new ArrayList<>();
    private List<ExperienceItem> experiences = new ArrayList<>();
    private List<EducationItem> educations = new ArrayList<>();
    private List<ProjectItem> projects = new ArrayList<>();
    private List<CertificationItem> certifications = new ArrayList<>();
    private List<AchievementItem> achievements = new ArrayList<>();

    public ResumeContentModel() {
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public List<String> getPrimarySkills() {
        return primarySkills;
    }

    public void setPrimarySkills(List<String> primarySkills) {
        this.primarySkills = primarySkills;
    }

    public List<String> getSecondarySkills() {
        return secondarySkills;
    }

    public void setSecondarySkills(List<String> secondarySkills) {
        this.secondarySkills = secondarySkills;
    }

    public List<ExperienceItem> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceItem> experiences) {
        this.experiences = experiences;
    }

    public List<EducationItem> getEducations() {
        return educations;
    }

    public void setEducations(List<EducationItem> educations) {
        this.educations = educations;
    }

    public List<ProjectItem> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectItem> projects) {
        this.projects = projects;
    }

    public List<CertificationItem> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CertificationItem> certifications) {
        this.certifications = certifications;
    }

    public List<AchievementItem> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementItem> achievements) {
        this.achievements = achievements;
    }
}
