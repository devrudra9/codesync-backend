package org.rudreshwar.codesync.versioncontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommitRequest {

    @NotBlank
    @Size(max = 200)
    private String message;

}