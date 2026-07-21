package org.rudreshwar.codesync.project.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.rudreshwar.codesync.project.entity.ProjectVisibility;

@Data
public class UpdateProjectRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String primaryLanguage;

    private ProjectVisibility visibility;

}