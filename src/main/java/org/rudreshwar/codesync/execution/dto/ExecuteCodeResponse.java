package org.rudreshwar.codesync.execution.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecuteCodeResponse {

    private String output;

    private String error;

    private Integer exitCode;

    private Long executionTimeMs;

    private String status;

}