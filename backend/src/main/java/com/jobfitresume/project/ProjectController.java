package com.jobfitresume.project;

import com.jobfitresume.common.ApiResponse;
import com.jobfitresume.project.dto.ProjectDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDto>>> getProjects() {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjects()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjectById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(@Valid @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Project created", projectService.createProject(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Project updated", projectService.updateProject(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.ok("Project deleted", null));
    }
}
