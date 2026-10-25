package com.jobfitresume.application.dto;

import java.util.Map;

public class ApplicationAnalyticsResponse {

    private int totalApplications;
    private int activeApplications;
    private int interviewsScheduled;
    private int offersReceived;
    private double responseRatePercentage;
    private Map<String, Integer> countByStatus;

    public ApplicationAnalyticsResponse() {
    }

    public ApplicationAnalyticsResponse(
            int totalApplications,
            int activeApplications,
            int interviewsScheduled,
            int offersReceived,
            double responseRatePercentage,
            Map<String, Integer> countByStatus) {
        this.totalApplications = totalApplications;
        this.activeApplications = activeApplications;
        this.interviewsScheduled = interviewsScheduled;
        this.offersReceived = offersReceived;
        this.responseRatePercentage = responseRatePercentage;
        this.countByStatus = countByStatus;
    }

    public int getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }

    public int getActiveApplications() {
        return activeApplications;
    }

    public void setActiveApplications(int activeApplications) {
        this.activeApplications = activeApplications;
    }

    public int getInterviewsScheduled() {
        return interviewsScheduled;
    }

    public void setInterviewsScheduled(int interviewsScheduled) {
        this.interviewsScheduled = interviewsScheduled;
    }

    public int getOffersReceived() {
        return offersReceived;
    }

    public void setOffersReceived(int offersReceived) {
        this.offersReceived = offersReceived;
    }

    public double getResponseRatePercentage() {
        return responseRatePercentage;
    }

    public void setResponseRatePercentage(double responseRatePercentage) {
        this.responseRatePercentage = responseRatePercentage;
    }

    public Map<String, Integer> getCountByStatus() {
        return countByStatus;
    }

    public void setCountByStatus(Map<String, Integer> countByStatus) {
        this.countByStatus = countByStatus;
    }
}
