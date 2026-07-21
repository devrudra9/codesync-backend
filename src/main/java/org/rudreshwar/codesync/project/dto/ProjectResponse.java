package org.rudreshwar.codesync.project.dto;

import lombok.Builder;
import lombok.Data;
import org.rudreshwar.codesync.project.entity.ProjectVisibility;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String primaryLanguage;
    private ProjectVisibility visibility;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentProjectId;
    private Integer forkCount;

}