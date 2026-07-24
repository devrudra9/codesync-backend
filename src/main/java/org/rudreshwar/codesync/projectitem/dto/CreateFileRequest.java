package org.rudreshwar.codesync.projectitem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.rudreshwar.codesync.projectitem.entity.ItemType;

@Data
public class CreateFileRequest {

    @NotBlank
    private String name;

    private ItemType type;

    private Long parentId;
}