package com.jobfitresume.resume.dto;

import java.util.ArrayList;
import java.util.List;

public class AiAssistantResponse {
    private String reply;
    private String action;
    private String modifiedSummary;
    private String emailSubject;
    private String emailBody;
    private List<String> suggestedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();

    public AiAssistantResponse() {}

    public AiAssistantResponse(String reply) {
        this.reply = reply;
        this.action = "GENERAL";
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModifiedSummary() {
        return modifiedSummary;
    }

    public void setModifiedSummary(String modifiedSummary) {
        this.modifiedSummary = modifiedSummary;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public List<String> getSuggestedSkills() {
        return suggestedSkills;
    }

    public void setSuggestedSkills(List<String> suggestedSkills) {
        this.suggestedSkills = suggestedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}
