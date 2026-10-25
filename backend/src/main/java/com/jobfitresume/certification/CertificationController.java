package com.jobfitresume.certification;

import com.jobfitresume.certification.dto.CertificationDto;
import com.jobfitresume.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificationDto>>> getCertifications() {
        return ResponseEntity.ok(ApiResponse.ok(certificationService.getCertifications()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationDto>> getCertificationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(certificationService.getCertificationById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificationDto>> createCertification(@Valid @RequestBody CertificationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Certification added", certificationService.createCertification(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationDto>> updateCertification(
            @PathVariable UUID id,
            @Valid @RequestBody CertificationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Certification updated", certificationService.updateCertification(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(@PathVariable UUID id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.ok(ApiResponse.ok("Certification deleted", null));
    }
}
