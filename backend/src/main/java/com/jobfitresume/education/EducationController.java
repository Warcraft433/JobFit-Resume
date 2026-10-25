package com.jobfitresume.education;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.education.dto.EducationDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EducationDto>>> getEducationList() {
        return ResponseEntity.ok(ApiResponse.ok(educationService.getEducationList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationDto>> getEducationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(educationService.getEducationById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EducationDto>> createEducation(@Valid @RequestBody EducationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Education record created", educationService.createEducation(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EducationDto>> updateEducation(
            @PathVariable UUID id,
            @Valid @RequestBody EducationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Education record updated", educationService.updateEducation(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@PathVariable UUID id) {
        educationService.deleteEducation(id);
        return ResponseEntity.ok(ApiResponse.ok("Education record deleted", null));
    }
}
