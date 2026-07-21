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
                .ownerUsername(project.getOwner().getUsername())
                .visibility(project.getVisibility())
                .forkCount(project.getForkCount())
                .parentProjectId(project.getParentProject() == null ? null : project.getParentProject().getId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

}