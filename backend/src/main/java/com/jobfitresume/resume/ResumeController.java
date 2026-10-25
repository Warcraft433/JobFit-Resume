package com.jobfitresume.resume;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.resume.dto.GenerateResumeRequest;
import com.jobfitresume.resume.dto.ResumeContentModel;
import com.jobfitresume.resume.dto.ResumeDto;
import com.jobfitresume.resume.dto.ResumeVersionDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.jobfitresume.resume.dto.AiAssistantRequest;
import com.jobfitresume.resume.dto.AiAssistantResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final AiResumeAssistantService aiResumeAssistantService;

    public ResumeController(ResumeService resumeService, AiResumeAssistantService aiResumeAssistantService) {
        this.resumeService = resumeService;
        this.aiResumeAssistantService = aiResumeAssistantService;
    }

    @PostMapping("/resume/assistant")
    public ResponseEntity<ApiResponse<AiAssistantResponse>> askAssistant(@RequestBody AiAssistantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Assistant responded", aiResumeAssistantService.processAssistantRequest(request)));
    }

    @PostMapping("/jobs/{id}/generate-resume")
    public ResponseEntity<ApiResponse<ResumeVersionDto>> generateResumeForJob(
            @PathVariable UUID id,
            @RequestBody(required = false) GenerateResumeRequest request) {
        ResumeVersionDto version = resumeService.generateResumeForJob(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Resume generated successfully", version));
    }

    @GetMapping("/resumes")
    public ResponseEntity<ApiResponse<List<ResumeDto>>> getResumes() {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.getResumes()));
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ApiResponse<ResumeDto>> getResumeById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.getResumeById(id)));
    }

    @PutMapping("/resumes/{id}")
    public ResponseEntity<ApiResponse<ResumeDto>> updateResume(
            @PathVariable UUID id,
            @Valid @RequestBody ResumeDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Resume updated", resumeService.updateResume(id, dto)));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@PathVariable UUID id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(ApiResponse.ok("Resume deleted", null));
    }

    @GetMapping("/resumes/{id}/versions")
    public ResponseEntity<ApiResponse<List<ResumeVersionDto>>> getVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.getVersions(id)));
    }

    @GetMapping("/resume-versions/{id}")
    public ResponseEntity<ApiResponse<ResumeVersionDto>> getVersion(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.getVersion(id)));
    }

    @PostMapping("/resumes/{id}/versions")
    public ResponseEntity<ApiResponse<ResumeVersionDto>> saveNewVersion(
            @PathVariable UUID id,
            @RequestBody ResumeContentModel content) {
        return ResponseEntity.ok(ApiResponse.ok("New resume version saved", resumeService.saveNewVersion(id, content)));
    }

    @GetMapping("/resume-versions/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable UUID id) {
        byte[] pdfBytes = resumeService.exportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"JobFit_Resume_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/resume-versions/{id}/export/docx")
    public ResponseEntity<byte[]> exportDocx(@PathVariable UUID id) {
        byte[] docxBytes = resumeService.exportDocx(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"JobFit_Resume_" + id + ".docx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(docxBytes);
    }
}
