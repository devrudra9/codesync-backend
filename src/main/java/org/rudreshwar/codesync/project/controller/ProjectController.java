package org.rudreshwar.codesync.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.response.ApiResponse;
import org.rudreshwar.codesync.project.dto.CreateProjectRequest;
import org.rudreshwar.codesync.project.dto.ProjectResponse;
import org.rudreshwar.codesync.project.dto.UpdateProjectRequest;
import org.rudreshwar.codesync.project.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request, Authentication authentication) {
        ProjectResponse response = projectService.createProject(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Project created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(Authentication authentication) {
        List<ProjectResponse> response = projectService.getMyProjects(authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Projects fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(@PathVariable Long id, Authentication authentication) {
        ProjectResponse response = projectService.getProject(id, authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Project fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request, Authentication authentication) {
        ProjectResponse response = projectService.updateProject(id, request, authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Project updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id, Authentication authentication) {
        projectService.deleteProject(id, authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Project deleted successfully", null));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getPublicProjects() {
        List<ProjectResponse> response = projectService.getPublicProjects();
        return ResponseEntity.ok(
                ApiResponse.success("Public Projects fetched successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProjectResponse>> searchProjects(
            @RequestParam(defaultValue = "") String q,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(projectService.searchPublicProjects(q, pageable));
    }

    @PostMapping("/{id}/fork")
    public ResponseEntity<ApiResponse<ProjectResponse>> forkProject(@PathVariable Long id, Authentication authentication) {
        ProjectResponse response = projectService.forkProject(id, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Project forked successfully", response));
    }

}