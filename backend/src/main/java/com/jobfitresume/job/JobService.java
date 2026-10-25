package com.jobfitresume.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.job.dto.*;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobAnalysisRepository jobAnalysisRepository;
    private final JobRequirementRepository jobRequirementRepository;
    private final JobMatchingService jobMatchingService;
    private final ObjectMapper objectMapper;

    public JobService(
            JobRepository jobRepository,
            JobAnalysisRepository jobAnalysisRepository,
            JobRequirementRepository jobRequirementRepository,
            JobMatchingService jobMatchingService,
            ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.jobAnalysisRepository = jobAnalysisRepository;
        this.jobRequirementRepository = jobRequirementRepository;
        this.jobMatchingService = jobMatchingService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<JobDto> getJobs() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<Job> jobs = jobRepository.findByUserIdOrderBySavedAtDesc(userId);
        return jobs.stream().map(job -> {
            JobDto dto = JobDto.fromEntity(job);
            jobAnalysisRepository.findTopByJobIdAndUserIdOrderByCreatedAtDesc(job.getId(), userId)
                    .ifPresent(analysis -> dto.setLatestMatchScore(analysis.getMatchScore()));
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public JobDto getJobById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        JobDto dto = JobDto.fromEntity(job);
        jobAnalysisRepository.findTopByJobIdAndUserIdOrderByCreatedAtDesc(job.getId(), userId)
                .ifPresent(analysis -> dto.setLatestMatchScore(analysis.getMatchScore()));
        return dto;
    }

    @Transactional
    public JobDto createJob(JobDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = new Job();
        job.setUserId(userId);
        dto.applyToEntity(job);
        Job saved = jobRepository.save(job);
        return JobDto.fromEntity(saved);
    }

    @Transactional
    public JobDto updateJob(UUID id, JobDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        dto.applyToEntity(job);
        Job updated = jobRepository.save(job);
        return JobDto.fromEntity(updated);
    }

    @Transactional
    public void deleteJob(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        jobRepository.delete(job);
    }

    @Transactional
    public JobAnalysisResponse analyzeJob(UUID jobId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        JobMatchingService.MatchingResult result = jobMatchingService.evaluate(userId, job);

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            jsonPayload = "{}";
        }

        JobAnalysis analysis = new JobAnalysis(userId, jobId, "1.0.0", jsonPayload, result.overallScore);
        JobAnalysis savedAnalysis = jobAnalysisRepository.save(analysis);

        // Delete prior requirements and save updated requirements
        for (JobRequirement req : result.requirementsToPersist) {
            req.setJobAnalysisId(savedAnalysis.getId());
            jobRequirementRepository.save(req);
        }

        JobAnalysisResponse response = new JobAnalysisResponse();
        response.setAnalysisId(savedAnalysis.getId());
        response.setJobId(jobId);
        response.setMatchScore(result.overallScore);
        response.setScoreBreakdown(result.breakdown);
        response.setRequirements(result.requirementDtos);
        response.setMatchedSkills(result.matchedSkills);
        response.setMissingSkills(result.missingSkills);
        response.setExperienceAssessment(result.experienceAssessment);
        response.setEducationAssessment(result.educationAssessment);
        response.setKeyResponsibilities(result.responsibilities);
        response.setAnalyzedAt(savedAnalysis.getCreatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public JobAnalysisResponse getLatestAnalysis(UUID jobId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        Optional<JobAnalysis> analysisOpt = jobAnalysisRepository.findTopByJobIdAndUserIdOrderByCreatedAtDesc(jobId, userId);
        if (analysisOpt.isEmpty()) {
            return analyzeJob(jobId);
        }

        JobAnalysis analysis = analysisOpt.get();
        List<JobRequirement> reqs = jobRequirementRepository.findByJobAnalysisId(analysis.getId());

        JobMatchingService.MatchingResult fresh = jobMatchingService.evaluate(userId, job);

        JobAnalysisResponse response = new JobAnalysisResponse();
        response.setAnalysisId(analysis.getId());
        response.setJobId(jobId);
        response.setMatchScore(analysis.getMatchScore());
        response.setScoreBreakdown(fresh.breakdown);
        response.setRequirements(fresh.requirementDtos);
        response.setMatchedSkills(fresh.matchedSkills);
        response.setMissingSkills(fresh.missingSkills);
        response.setExperienceAssessment(fresh.experienceAssessment);
        response.setEducationAssessment(fresh.educationAssessment);
        response.setKeyResponsibilities(fresh.responsibilities);
        response.setAnalyzedAt(analysis.getCreatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public SkillsGapResponse getSkillsGap(UUID jobId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        JobMatchingService.MatchingResult result = jobMatchingService.evaluate(userId, job);

        SkillsGapResponse res = new SkillsGapResponse();
        res.setJobId(jobId);
        res.setOverallMatchScore(result.overallScore);
        res.setMatchedSkills(result.matchedSkills);
        res.setMissingRequiredSkills(result.missingSkills);
        res.setMissingPreferredSkills(result.missingPreferredSkills);
        res.setTotalSkillsRequired(result.matchedSkills.size() + result.missingSkills.size());
        res.setMatchedSkillsCount(result.matchedSkills.size());
        res.setMissingSkillsCount(result.missingSkills.size());
        res.setRecommendations(result.recommendations);

        return res;
    }

    @Transactional(readOnly = true)
    public List<LearningRecommendationDto> getLearningRecommendations(UUID jobId) {
        return getSkillsGap(jobId).getRecommendations();
    }
}
