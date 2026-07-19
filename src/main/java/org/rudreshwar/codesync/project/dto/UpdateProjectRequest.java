package org.rudreshwar.codesync.project.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProjectRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String primaryLanguage;

}