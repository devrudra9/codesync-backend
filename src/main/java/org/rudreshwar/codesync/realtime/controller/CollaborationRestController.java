package org.rudreshwar.codesync.realtime.controller;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.response.ApiResponse;
import org.rudreshwar.codesync.realtime.service.CollaborationSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class CollaborationRestController {

    private final CollaborationSessionService sessionService;

    @GetMapping("/{projectId}/active-users")
    public ResponseEntity<ApiResponse<Set<String>>> getActiveUsers(@PathVariable Long projectId) {
        return ResponseEntity.ok(
                ApiResponse.success("Fetched active users successfully", sessionService.getActiveUsers(projectId))
        );
    }

}
