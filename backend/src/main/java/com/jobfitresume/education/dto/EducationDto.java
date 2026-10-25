package com.jobfitresume.education.dto;

import com.jobfitresume.education.Education;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class EducationDto {

    private UUID id;

    @NotBlank(message = "Institution name is required")
    private String institution;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private boolean currentlyStudying;
    private String gradeType;
    private String gradeValue;
    private boolean showGradeOnResume = true;
    private String coursework;
    private String description;
    private int displayOrder;

    public EducationDto() {
    }

    public static EducationDto fromEntity(Education entity) {
        EducationDto dto = new EducationDto();
        dto.setId(entity.getId());
        dto.setInstitution(entity.getInstitution());
        dto.setDegree(entity.getDegree());
        dto.setFieldOfStudy(entity.getFieldOfStudy());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCurrentlyStudying(entity.isCurrentlyStudying());
        dto.setGradeType(entity.getGradeType());
        dto.setGradeValue(entity.getGradeValue());
        dto.setShowGradeOnResume(entity.isShowGradeOnResume());
        dto.setCoursework(entity.getCoursework());
        dto.setDescription(entity.getDescription());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }

    public void applyToEntity(Education entity) {
        entity.setInstitution(this.institution);
        entity.setDegree(this.degree);
        entity.setFieldOfStudy(this.fieldOfStudy);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setCurrentlyStudying(this.currentlyStudying);
        entity.setGradeType(this.gradeType);
        entity.setGradeValue(this.gradeValue);
        entity.setShowGradeOnResume(this.showGradeOnResume);
        entity.setCoursework(this.coursework);
        entity.setDescription(this.description);
        entity.setDisplayOrder(this.displayOrder);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isCurrentlyStudying() {
        return currentlyStudying;
    }

    public void setCurrentlyStudying(boolean currentlyStudying) {
        this.currentlyStudying = currentlyStudying;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public boolean isShowGradeOnResume() {
        return showGradeOnResume;
    }

    public void setShowGradeOnResume(boolean showGradeOnResume) {
        this.showGradeOnResume = showGradeOnResume;
    }

    public String getCoursework() {
        return coursework;
    }

    public void setCoursework(String coursework) {
        this.coursework = coursework;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
