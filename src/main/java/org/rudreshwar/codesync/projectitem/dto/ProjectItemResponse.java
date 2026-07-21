package org.rudreshwar.codesync.projectitem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectItemResponse {

    private Long id;
    private String name;
    private String type;
    private Long parentId;
    private Long projectId;
    private String content;

}