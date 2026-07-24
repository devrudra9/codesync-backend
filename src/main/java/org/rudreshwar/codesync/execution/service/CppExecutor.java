package org.rudreshwar.codesync.execution.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;
import org.rudreshwar.codesync.execution.executor.FileManager;
import org.rudreshwar.codesync.execution.executor.ProcessRunner;
import org.springframework.stereotype.Component;

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
        return null;
    }

}
