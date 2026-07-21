package org.rudreshwar.codesync.projectitem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProjectItemTreeResponse {

    private Long id;

    private String name;

    private String type;

    private Long parentId;

    @Builder.Default
    private List<ProjectItemTreeResponse> children = new ArrayList<>();
}