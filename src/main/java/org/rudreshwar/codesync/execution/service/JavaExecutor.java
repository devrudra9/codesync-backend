package org.rudreshwar.codesync.execution.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;
import org.rudreshwar.codesync.execution.executor.FileManager;
import org.rudreshwar.codesync.execution.executor.ProcessResult;
import org.rudreshwar.codesync.execution.executor.ProcessRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JavaExecutor implements LanguageExecutor {

    private final ProcessRunner processRunner;

    private final FileManager fileManager;

    @Override
    public String language() {
        return "java";
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        Path workspace = null;
        try {
            workspace = fileManager.createWorkspace();
            Path sourceFile = workspace.resolve("Main.java");
            Files.writeString(sourceFile, request.getCode());
            ProcessResult compileResult = processRunner.run(
                    List.of(
                            "javac",
                            sourceFile.toAbsolutePath().toString()
                    ),
                    null,
                    workspace
            );

            if (compileResult.getExitCode() != 0) {
                return ExecuteCodeResponse.builder()
                        .output("")
                        .error(compileResult.getError())
                        .exitCode(compileResult.getExitCode())
                        .executionTimeMs(compileResult.getExecutionTimeMs())
                        .status("ERROR")
                        .build();

            }

            ProcessResult executionResult = processRunner.run(
                    List.of(
                            "java",
                            "-cp",
                            workspace.toAbsolutePath().toString(),
                            "Main"
                    ),
                    request.getInput(),
                    workspace
            );

            return ExecuteCodeResponse.builder()
                    .output(executionResult.getOutput())
                    .error(executionResult.getError())
                    .exitCode(executionResult.getExitCode())
                    .executionTimeMs(executionResult.getExecutionTimeMs())
                    .status(executionResult.isTimedOut() ? "TIMEOUT" : "SUCCESS")
                    .build();

        } catch (Exception e) {
            return ExecuteCodeResponse.builder()
                    .output("")
                    .error(e.getMessage())
                    .exitCode(-1)
                    .executionTimeMs(0L)
                    .status("ERROR")
                    .build();
        } finally {
            if (workspace != null) {
                try {
                    fileManager.deleteWorkspace(workspace);
                } catch (Exception ignored) {}
            }
        }
    }

}