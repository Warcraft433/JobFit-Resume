package com.jobfitresume.experience;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.experience.dto.ExperienceDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExperienceDto>>> getExperienceList() {
        return ResponseEntity.ok(ApiResponse.ok(experienceService.getExperienceList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceDto>> getExperienceById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(experienceService.getExperienceById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceDto>> createExperience(@Valid @RequestBody ExperienceDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Experience record created", experienceService.createExperience(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExperienceDto>> updateExperience(
            @PathVariable UUID id,
            @Valid @RequestBody ExperienceDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Experience record updated", experienceService.updateExperience(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(@PathVariable UUID id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok(ApiResponse.ok("Experience record deleted", null));
    }
}
