package org.rudreshwar.codesync.versioncontrol.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.response.ApiResponse;
import org.rudreshwar.codesync.versioncontrol.dto.CommitResponse;
import org.rudreshwar.codesync.versioncontrol.dto.CreateCommitRequest;
import org.rudreshwar.codesync.versioncontrol.service.CommitService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class CommitController {

    private final CommitService versionControlService;

    @PostMapping("/{projectId}/commits")
    public ResponseEntity<ApiResponse<CommitResponse>> createCommit(
            @PathVariable Long projectId, @Valid @RequestBody CreateCommitRequest request, Authentication authentication) {
        CommitResponse response = versionControlService.createCommit(projectId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Commit created successfully", response));
    }

    @GetMapping("/{projectId}/commits")
    public ResponseEntity<ApiResponse<List<CommitResponse>>> getCommitHistory(@PathVariable Long projectId, Authentication authentication) {
        List<CommitResponse> response = versionControlService.getCommitHistory(projectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Commit history fetched successfully", response));
    }

    @PostMapping("/{projectId}/commits/{commitId}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreCommit(
            @PathVariable Long projectId, @PathVariable Long commitId, Authentication authentication) {
        versionControlService.restoreCommit(projectId, commitId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Commit restored successfully", null));
    }
}
