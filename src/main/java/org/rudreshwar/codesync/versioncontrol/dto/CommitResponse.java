package org.rudreshwar.codesync.versioncontrol.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommitResponse {

    private Long id;
    private String commitHash;
    private String message;
    private String author;
    private LocalDateTime committedAt;

}