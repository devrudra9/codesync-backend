package org.rudreshwar.codesync.execution.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExecuteCodeRequest {

    @NotBlank
    private String language;

    @NotBlank
    private String code;

    private String input;

    private Long projectId;

}