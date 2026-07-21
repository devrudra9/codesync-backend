package org.rudreshwar.codesync.projectitem.mapper;

import org.rudreshwar.codesync.projectitem.dto.ProjectItemResponse;
import org.rudreshwar.codesync.projectitem.dto.ProjectItemTreeResponse;
import org.rudreshwar.codesync.projectitem.entity.ProjectItem;
import org.springframework.stereotype.Component;

@Component
public class ProjectItemMapper {

    public ProjectItemResponse toResponse(ProjectItem file) {
        return ProjectItemResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .type(file.getType().name())
                .content(file.getContent())
                .projectId(file.getProject().getId())
                .parentId(file.getParent() != null ? file.getParent().getId() : null)
                .build();
    }

    public ProjectItemTreeResponse toTree(ProjectItem item) {
        return ProjectItemTreeResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .type(item.getType().name())
                .parentId(item.getParent() != null ? item.getParent().getId() : null)
                .build();
    }
}