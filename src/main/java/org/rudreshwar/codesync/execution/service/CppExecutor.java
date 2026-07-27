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
public class CppExecutor implements LanguageExecutor {

    private final ProcessRunner processRunner;

    private final FileManager fileManager;

    @Override
    public String language() {
        return "cpp";
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        Path workspace = null;
        try {
            workspace = fileManager.createWorkspace();
            Path source = workspace.resolve("main.cpp");
            Files.writeString(source, request.getCode());
            ProcessResult compile = processRunner.run(
                    List.of(
                            "g++",
                            "main.cpp",
                            "-o",
                            "main.exe"
                    ),
                    null,
                    workspace);

            if (compile.getExitCode() != 0) {
                return ExecuteCodeResponse.builder()
                        .output("")
                        .error(compile.getError())
                        .exitCode(compile.getExitCode())
                        .executionTimeMs(compile.getExecutionTimeMs())
                        .status("ERROR")
                        .build();

            }
            ProcessResult run = processRunner.run(
                    List.of(
                            workspace.resolve("main.exe")
                                    .toAbsolutePath()
                                    .toString()
                    ),
                    request.getInput(),
                    workspace);

            return ExecuteCodeResponse.builder()
                    .output(run.getOutput())
                    .error(run.getError())
                    .exitCode(run.getExitCode())
                    .executionTimeMs(run.getExecutionTimeMs())
                    .status(run.isTimedOut() ? "TIMEOUT" : "SUCCESS")
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
