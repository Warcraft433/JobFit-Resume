package com.jobfitresume.resume;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.job.Job;
import com.jobfitresume.job.JobRepository;
import com.jobfitresume.resume.dto.*;
import com.jobfitresume.resume.export.DocxResumeExporter;
import com.jobfitresume.resume.export.PdfResumeExporter;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final JobRepository jobRepository;
    private final ResumeTailoringService resumeTailoringService;
    private final PdfResumeExporter pdfResumeExporter;
    private final DocxResumeExporter docxResumeExporter;
    private final ObjectMapper objectMapper;

    public ResumeService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            JobRepository jobRepository,
            ResumeTailoringService resumeTailoringService,
            PdfResumeExporter pdfResumeExporter,
            DocxResumeExporter docxResumeExporter,
            ObjectMapper objectMapper) {
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.jobRepository = jobRepository;
        this.resumeTailoringService = resumeTailoringService;
        this.pdfResumeExporter = pdfResumeExporter;
        this.docxResumeExporter = docxResumeExporter;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResumeVersionDto generateResumeForJob(UUID jobId, GenerateResumeRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Job job = null;
        if (jobId != null) {
            job = jobRepository.findByIdAndUserId(jobId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        }

        String template = (request != null && request.getTemplateKey() != null) ? request.getTemplateKey() : "classic";
        String title = (request != null && request.getTitle() != null && !request.getTitle().trim().isEmpty())
                ? request.getTitle().trim()
                : (job != null ? "Resume - " + job.getJobTitle() + " (" + job.getCompany() + ")" : "General Technical Resume");

        // Tailor content
        ResumeContentModel content = resumeTailoringService.buildTailoredContent(userId, job, request);

        // Find or create Resume
        Resume resume;
        if (job != null) {
            List<Resume> existing = resumeRepository.findByUserIdAndJobId(userId, job.getId());
            if (!existing.isEmpty()) {
                resume = existing.get(0);
                resume.setTitle(title);
                resume.setTemplateKey(template);
            } else {
                resume = new Resume(userId, job.getId(), title, template);
            }
        } else {
            resume = new Resume(userId, null, title, template);
        }
        Resume savedResume = resumeRepository.save(resume);

        // Find latest version number
        Optional<ResumeVersion> latestVer = resumeVersionRepository.findTopByResumeIdOrderByVersionNumberDesc(savedResume.getId());
        int nextVersionNumber = latestVer.map(v -> v.getVersionNumber() + 1).orElse(1);

        String jsonContent;
        try {
            jsonContent = objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            jsonContent = "{}";
        }

        ResumeVersion version = new ResumeVersion(savedResume.getId(), nextVersionNumber, jsonContent, null, null);
        ResumeVersion savedVersion = resumeVersionRepository.save(version);

        ResumeVersionDto dto = ResumeVersionDto.fromEntity(savedVersion);
        dto.setContent(content);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ResumeDto> getResumes() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<Resume> list = resumeRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return list.stream().map(r -> {
            ResumeDto dto = ResumeDto.fromEntity(r);
            resumeVersionRepository.findTopByResumeIdOrderByVersionNumberDesc(r.getId())
                    .ifPresent(v -> dto.setLatestVersionNumber(v.getVersionNumber()));
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public ResumeDto getResumeById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Resume resume = resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        ResumeDto dto = ResumeDto.fromEntity(resume);
        resumeVersionRepository.findTopByResumeIdOrderByVersionNumberDesc(resume.getId())
                .ifPresent(v -> dto.setLatestVersionNumber(v.getVersionNumber()));
        return dto;
    }

    @Transactional
    public ResumeDto updateResume(UUID id, ResumeDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Resume resume = resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        resume.setTitle(dto.getTitle());
        if (dto.getTemplateKey() != null) {
            resume.setTemplateKey(dto.getTemplateKey());
        }
        Resume updated = resumeRepository.save(resume);
        return ResumeDto.fromEntity(updated);
    }

    @Transactional
    public void deleteResume(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Resume resume = resumeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        resumeRepository.delete(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionDto> getVersions(UUID resumeId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        return resumeVersionRepository.findByResumeIdOrderByVersionNumberDesc(resumeId)
                .stream()
                .map(v -> {
                    ResumeVersionDto dto = ResumeVersionDto.fromEntity(v);
                    try {
                        dto.setContent(objectMapper.readValue(v.getContentJson(), ResumeContentModel.class));
                    } catch (Exception ignored) {
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeVersionDto getVersion(UUID versionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found"));

        resumeRepository.findByIdAndUserId(version.getResumeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized"));

        ResumeVersionDto dto = ResumeVersionDto.fromEntity(version);
        try {
            dto.setContent(objectMapper.readValue(version.getContentJson(), ResumeContentModel.class));
        } catch (Exception e) {
            dto.setContent(new ResumeContentModel());
        }
        return dto;
    }

    @Transactional
    public ResumeVersionDto saveNewVersion(UUID resumeId, ResumeContentModel content) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Resume resume = resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        Optional<ResumeVersion> latest = resumeVersionRepository.findTopByResumeIdOrderByVersionNumberDesc(resumeId);
        int nextVersionNumber = latest.map(v -> v.getVersionNumber() + 1).orElse(1);

        String json;
        try {
            json = objectMapper.writeValueAsString(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize resume content: " + e.getMessage());
        }

        ResumeVersion newVer = new ResumeVersion(resumeId, nextVersionNumber, json, null, null);
        ResumeVersion saved = resumeVersionRepository.save(newVer);

        ResumeVersionDto dto = ResumeVersionDto.fromEntity(saved);
        dto.setContent(content);
        return dto;
    }

    @Transactional(readOnly = true)
    public byte[] exportPdf(UUID versionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found"));

        Resume resume = resumeRepository.findByIdAndUserId(version.getResumeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized"));

        ResumeContentModel model;
        try {
            model = objectMapper.readValue(version.getContentJson(), ResumeContentModel.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize resume content: " + e.getMessage());
        }

        return pdfResumeExporter.exportToPdf(model, resume.getTemplateKey());
    }

    @Transactional(readOnly = true)
    public byte[] exportDocx(UUID versionId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        ResumeVersion version = resumeVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found"));

        resumeRepository.findByIdAndUserId(version.getResumeId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or unauthorized"));

        ResumeContentModel model;
        try {
            model = objectMapper.readValue(version.getContentJson(), ResumeContentModel.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize resume content: " + e.getMessage());
        }

        return docxResumeExporter.exportToDocx(model);
    }
}
