package org.rudreshwar.codesync.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String primaryLanguage;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}