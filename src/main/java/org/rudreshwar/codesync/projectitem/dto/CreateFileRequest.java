package org.rudreshwar.codesync.projectitem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.rudreshwar.codesync.projectitem.entity.FileType;

@Data
public class CreateFileRequest {

    @NotBlank
    private String name;

    private FileType type;

    private Long parentId;
}