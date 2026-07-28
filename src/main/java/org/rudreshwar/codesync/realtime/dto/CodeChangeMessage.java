package org.rudreshwar.codesync.realtime.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeChangeMessage {

    private Long projectId;

    private Long itemId;

    private String username;

    private String content;
}