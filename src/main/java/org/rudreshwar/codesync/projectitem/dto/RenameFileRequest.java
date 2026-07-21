package org.rudreshwar.codesync.projectitem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenameFileRequest {

    @NotBlank
    private String name;
}