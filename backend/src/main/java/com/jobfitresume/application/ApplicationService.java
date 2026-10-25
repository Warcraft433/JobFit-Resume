package com.jobfitresume.application;

import com.jobfitresume.application.dto.ApplicationAnalyticsResponse;
import com.jobfitresume.application.dto.ApplicationDto;
import com.jobfitresume.application.dto.ApplicationStatusUpdateRequest;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.job.Job;
import com.jobfitresume.job.JobRepository;
import com.jobfitresume.resume.ResumeVersion;
import com.jobfitresume.resume.ResumeVersionRepository;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ResumeVersionRepository resumeVersionRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            JobRepository jobRepository,
            ResumeVersionRepository resumeVersionRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.resumeVersionRepository = resumeVersionRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplications() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return applicationRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::enrichDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationDto getApplicationById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Application app = applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return enrichDto(app);
    }

    @Transactional
    public ApplicationDto createApplication(ApplicationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        // Verify job ownership
        jobRepository.findByIdAndUserId(dto.getJobId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        Application app = new Application();
        app.setUserId(userId);
        dto.applyToEntity(app);
        if (app.getStatus() == null || app.getStatus().isEmpty()) {
            app.setStatus("SAVED");
        }
        if ("APPLIED".equalsIgnoreCase(app.getStatus()) && app.getAppliedAt() == null) {
            app.setAppliedAt(Instant.now());
        }

        Application saved = applicationRepository.save(app);
        return enrichDto(saved);
    }

    @Transactional
    public ApplicationDto updateApplication(UUID id, ApplicationDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Application app = applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        dto.applyToEntity(app);
        Application updated = applicationRepository.save(app);
        return enrichDto(updated);
    }

    @Transactional
    public ApplicationDto updateStatus(UUID id, ApplicationStatusUpdateRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Application app = applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        String newStatus = request.getStatus().trim().toUpperCase();
        app.setStatus(newStatus);
        if ("APPLIED".equals(newStatus) && app.getAppliedAt() == null) {
            app.setAppliedAt(request.getAppliedAt() != null ? request.getAppliedAt() : Instant.now());
        } else if (request.getAppliedAt() != null) {
            app.setAppliedAt(request.getAppliedAt());
        }

        if (request.getFollowUpAt() != null) {
            app.setFollowUpAt(request.getFollowUpAt());
        }
        if (request.getNotes() != null) {
            app.setNotes(request.getNotes());
        }

        Application updated = applicationRepository.save(app);
        return enrichDto(updated);
    }

    @Transactional
    public void deleteApplication(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Application app = applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        applicationRepository.delete(app);
    }

    @Transactional(readOnly = true)
    public ApplicationAnalyticsResponse getAnalytics() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<Application> apps = applicationRepository.findByUserIdOrderByUpdatedAtDesc(userId);

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("SAVED", 0);
        statusCounts.put("PREPARING", 0);
        statusCounts.put("APPLIED", 0);
        statusCounts.put("INTERVIEW", 0);
        statusCounts.put("OFFER", 0);
        statusCounts.put("REJECTED", 0);
        statusCounts.put("WITHDRAWN", 0);

        int total = apps.size();
        int active = 0;
        int interviews = 0;
        int offers = 0;
        int appliedTotal = 0;

        for (Application a : apps) {
            String s = a.getStatus().toUpperCase();
            statusCounts.put(s, statusCounts.getOrDefault(s, 0) + 1);

            if (!"REJECTED".equals(s) && !"WITHDRAWN".equals(s)) {
                active++;
            }
            if ("INTERVIEW".equals(s)) {
                interviews++;
            }
            if ("OFFER".equals(s)) {
                offers++;
            }
            if ("APPLIED".equals(s) || "INTERVIEW".equals(s) || "OFFER".equals(s) || "REJECTED".equals(s)) {
                appliedTotal++;
            }
        }

        double responseRate = appliedTotal > 0 ? ((double) (interviews + offers) / appliedTotal) * 100.0 : 0.0;
        responseRate = Math.round(responseRate * 10.0) / 10.0;

        return new ApplicationAnalyticsResponse(total, active, interviews, offers, responseRate, statusCounts);
    }

    private ApplicationDto enrichDto(Application entity) {
        ApplicationDto dto = ApplicationDto.fromEntity(entity);
        jobRepository.findById(entity.getJobId()).ifPresent(job -> {
            dto.setJobTitle(job.getJobTitle());
            dto.setCompany(job.getCompany());
            dto.setLocation(job.getLocation());
        });

        if (entity.getResumeVersionId() != null) {
            resumeVersionRepository.findById(entity.getResumeVersionId())
                    .ifPresent(v -> dto.setResumeVersionNumber(v.getVersionNumber()));
        }

        return dto;
    }
}
