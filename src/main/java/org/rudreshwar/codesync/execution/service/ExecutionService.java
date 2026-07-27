package org.rudreshwar.codesync.execution.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;
import org.rudreshwar.codesync.execution.entity.Execution;
import org.rudreshwar.codesync.execution.entity.ExecutionStatus;
import org.rudreshwar.codesync.execution.repository.ExecutionRepository;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final List<LanguageExecutor> executors;
    private final ExecutionRepository executionRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private LanguageExecutor getExecutor(String language) {
        return executors.stream()
                .filter(executor -> executor.language().equalsIgnoreCase(language))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language: " + language));
    }

    @Transactional
    public ExecuteCodeResponse execute(ExecuteCodeRequest request, String username) {
        LanguageExecutor executor = getExecutor(request.getLanguage());

        ExecuteCodeResponse response = executor.execute(request);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = null;

        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElse(null);
        }

        Execution execution = Execution.builder()
                .language(request.getLanguage())
                .sourceCode(request.getCode())
                .output(response.getOutput())
                .errorOutput(response.getError())
                .exitCode(response.getExitCode())
                .executionTimeMs(response.getExecutionTimeMs())
                .executedAt(LocalDateTime.now())
                .status(ExecutionStatus.valueOf(response.getStatus()))
                .user(user)
                .project(project)
                .build();

        executionRepository.save(execution);

        return response;

    }

}