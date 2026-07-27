package org.rudreshwar.codesync.execution.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.response.ApiResponse;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;
import org.rudreshwar.codesync.execution.service.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/execution")
@RequiredArgsConstructor
public class ExecutionController {

    private final ExecutionService executionService;

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<ExecuteCodeResponse>> execute(
            @Valid @RequestBody ExecuteCodeRequest request, Authentication authentication) {
        ExecuteCodeResponse response = executionService.execute(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Execution completed", response));
    }

}
