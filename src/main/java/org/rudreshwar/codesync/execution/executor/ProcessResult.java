package org.rudreshwar.codesync.execution.executor;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessResult {

    private String output;
    private String error;
    private Integer exitCode;
    private Long executionTimeMs;
    private boolean timedOut;

}