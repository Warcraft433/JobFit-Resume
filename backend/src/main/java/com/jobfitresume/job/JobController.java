package com.jobfitresume.job;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.job.dto.JobAnalysisResponse;
import com.jobfitresume.job.dto.JobDto;
import com.jobfitresume.job.dto.LearningRecommendationDto;
import com.jobfitresume.job.dto.SkillsGapResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDto>>> getJobs() {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJobs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDto>> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJobById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobDto>> createJob(@Valid @RequestBody JobDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Job saved successfully", jobService.createJob(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobDto>> updateJob(
            @PathVariable UUID id,
            @Valid @RequestBody JobDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Job updated successfully", jobService.updateJob(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable UUID id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Job deleted successfully", null));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<ApiResponse<JobAnalysisResponse>> analyzeJob(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Job analyzed successfully", jobService.analyzeJob(id)));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<ApiResponse<JobAnalysisResponse>> getJobAnalysis(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getLatestAnalysis(id)));
    }

    @GetMapping("/{id}/skills-gap")
    public ResponseEntity<ApiResponse<SkillsGapResponse>> getSkillsGap(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getSkillsGap(id)));
    }

    @GetMapping("/{id}/learning-recommendations")
    public ResponseEntity<ApiResponse<List<LearningRecommendationDto>>> getLearningRecommendations(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getLearningRecommendations(id)));
    }
}
