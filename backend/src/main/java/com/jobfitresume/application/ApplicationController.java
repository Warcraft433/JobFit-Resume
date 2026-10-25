package com.jobfitresume.application;

import com.jobfitresume.application.dto.ApplicationAnalyticsResponse;
import com.jobfitresume.application.dto.ApplicationDto;
import com.jobfitresume.application.dto.ApplicationStatusUpdateRequest;
import com.jobfitresume.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getApplications() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplications()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDto>> getApplicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getApplicationById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationDto>> createApplication(@Valid @RequestBody ApplicationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Application created", applicationService.createApplication(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDto>> updateApplication(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Application updated", applicationService.updateApplication(id, dto)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Application status updated", applicationService.updateStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(ApiResponse.ok("Application deleted", null));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<ApplicationAnalyticsResponse>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.getAnalytics()));
    }
}
