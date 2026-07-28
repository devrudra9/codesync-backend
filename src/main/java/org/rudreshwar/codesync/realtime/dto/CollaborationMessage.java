package org.rudreshwar.codesync.realtime.dto;

import lombok.*;
import org.rudreshwar.codesync.realtime.enums.CollaborationOperation;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaborationMessage {

    private Long projectId;
    private Long itemId;
    private String username;
    private String content;
    private CollaborationOperation operation;
    private Integer line;
    private Integer column;
    private LocalDateTime timestamp;

}