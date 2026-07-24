package org.rudreshwar.codesync.execution.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;
import org.rudreshwar.codesync.execution.executor.FileManager;
import org.rudreshwar.codesync.execution.executor.ProcessRunner;
import org.springframework.stereotype.Component;

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
        return null;
    }

}