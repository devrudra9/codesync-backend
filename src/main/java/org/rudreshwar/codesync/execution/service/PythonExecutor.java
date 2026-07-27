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
public class PythonExecutor implements LanguageExecutor {

    private final ProcessRunner processRunner;

    private final FileManager fileManager;

    @Override
    public String language() {
        return "python";
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        Path workspace = null;
        try {
            workspace = fileManager.createWorkspace();
            Path sourceFile = workspace.resolve("main.py");
            Files.writeString(sourceFile, request.getCode());
            ProcessResult result = processRunner.run(
                    List.of("python", "main.py"),
                    request.getInput(),
                    workspace);

            return ExecuteCodeResponse.builder()
                    .output(result.getOutput())
                    .error(result.getError())
                    .exitCode(result.getExitCode())
                    .executionTimeMs(result.getExecutionTimeMs())
                    .status(result.isTimedOut() ? "TIMEOUT" : "SUCCESS")
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