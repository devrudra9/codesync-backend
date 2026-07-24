package org.rudreshwar.codesync.execution.service;

import org.rudreshwar.codesync.execution.dto.ExecuteCodeRequest;
import org.rudreshwar.codesync.execution.dto.ExecuteCodeResponse;

public interface LanguageExecutor {

    String language();

    ExecuteCodeResponse execute(ExecuteCodeRequest request);

}