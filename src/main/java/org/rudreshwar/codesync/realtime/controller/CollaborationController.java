package org.rudreshwar.codesync.realtime.controller;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.realtime.dto.CollaborationMessage;
import org.rudreshwar.codesync.realtime.service.CollaborationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CollaborationController {

    private final SimpMessagingTemplate messagingTemplate;

    private final CollaborationService collaborationService;

    @MessageMapping("/collaborate")
    public void collaborate(CollaborationMessage message) {

        collaborationService.broadcast(message);
    }
}