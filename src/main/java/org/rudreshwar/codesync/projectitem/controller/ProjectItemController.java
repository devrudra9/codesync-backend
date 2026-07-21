package org.rudreshwar.codesync.projectitem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.response.ApiResponse;
import org.rudreshwar.codesync.projectitem.dto.CreateFileRequest;
import org.rudreshwar.codesync.projectitem.dto.ProjectItemResponse;
import org.rudreshwar.codesync.projectitem.dto.ProjectItemTreeResponse;
import org.rudreshwar.codesync.projectitem.service.ProjectItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectItemController {

    private final ProjectItemService projectItemService;

    @PostMapping("/{projectId}/items")
    public ResponseEntity<ApiResponse<ProjectItemResponse>> createItem(
            @PathVariable Long projectId, @Valid @RequestBody CreateFileRequest request, Authentication authentication) {
        ProjectItemResponse response = projectItemService.createItem(projectId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created successfully", response));
    }

    @GetMapping("/{projectId}/items")
    public ResponseEntity<ApiResponse<List<ProjectItemTreeResponse>>> getProjectTree(
            @PathVariable Long projectId, Authentication authentication) {
        List<ProjectItemTreeResponse> response = projectItemService.getProjectTree(projectId, authentication.getName());
        return ResponseEntity.ok(
                ApiResponse.success("Project tree fetched successfully", response)
        );
    }
}