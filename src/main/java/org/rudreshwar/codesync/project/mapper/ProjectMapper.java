package org.rudreshwar.codesync.project.mapper;

import org.rudreshwar.codesync.project.dto.ProjectResponse;
import org.rudreshwar.codesync.project.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .primaryLanguage(project.getPrimaryLanguage())
                .visibility(project.getVisibility().name())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

}