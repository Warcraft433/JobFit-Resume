package com.jobfitresume.resume.dto;

import java.util.UUID;

public class AiAssistantRequest {
    private String prompt;
    private UUID targetJobId;
    private String currentSummary;
    private String tone; // "professional", "concise", "warm"
    private String action; // "GENERAL", "POLISH_SUMMARY", "SHORTEN_SUMMARY", "TAILOR_SKILLS", "DRAFT_EMAIL"

    public AiAssistantRequest() {}

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public UUID getTargetJobId() {
        return targetJobId;
    }

    public void setTargetJobId(UUID targetJobId) {
        this.targetJobId = targetJobId;
    }

    public String getCurrentSummary() {
        return currentSummary;
    }

    public void setCurrentSummary(String currentSummary) {
        this.currentSummary = currentSummary;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
