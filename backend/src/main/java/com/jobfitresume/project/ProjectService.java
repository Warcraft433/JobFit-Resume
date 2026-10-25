package com.jobfitresume.project;

import com.jobfitresume.common.ResourceNotFoundException;
import com.jobfitresume.project.dto.ProjectDto;
import com.jobfitresume.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjects() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return projectRepository.findByUserIdOrderByDisplayOrderAscCreatedAtDesc(userId)
                .stream()
                .map(ProjectDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Project project = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return ProjectDto.fromEntity(project);
    }

    @Transactional
    public ProjectDto createProject(ProjectDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Project entity = new Project();
        entity.setUserId(userId);
        dto.applyToEntity(entity);
        Project saved = projectRepository.save(entity);
        return ProjectDto.fromEntity(saved);
    }

    @Transactional
    public ProjectDto updateProject(UUID id, ProjectDto dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Project entity = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        dto.applyToEntity(entity);
        Project updated = projectRepository.save(entity);
        return ProjectDto.fromEntity(updated);
    }

    @Transactional
    public void deleteProject(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Project entity = projectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectRepository.delete(entity);
    }
}
