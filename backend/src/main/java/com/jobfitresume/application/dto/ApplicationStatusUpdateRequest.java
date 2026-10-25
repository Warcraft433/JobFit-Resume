package com.jobfitresume.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class ApplicationStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private Instant appliedAt;
    private Instant followUpAt;
    private String notes;

    public ApplicationStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Instant getFollowUpAt() {
        return followUpAt;
    }

    public void setFollowUpAt(Instant followUpAt) {
        this.followUpAt = followUpAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
