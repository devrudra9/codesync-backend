package org.rudreshwar.codesync.realtime.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.entity.ProjectVisibility;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.projectitem.entity.ProjectItem;
import org.rudreshwar.codesync.projectitem.repository.ProjectItemRepository;
import org.rudreshwar.codesync.realtime.dto.CollaborationMessage;
import org.rudreshwar.codesync.realtime.enums.CollaborationOperation;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CollaborationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final CollaborationSessionService sessionService;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectItemRepository projectItemRepository;

    public void broadcast(CollaborationMessage message) {

        validateProjectAccess(message.getProjectId(), message.getUsername());

        if (message.getOperation() == CollaborationOperation.JOIN) {
            sessionService.joinProject(
                    message.getProjectId(),
                    message.getUsername());
        }

        if (message.getOperation() == CollaborationOperation.LEAVE) {
            sessionService.leaveProject(
                    message.getProjectId(),
                    message.getUsername());
        }

        if (message.getOperation() == CollaborationOperation.EDIT) {
            ProjectItem item = projectItemRepository.findById(message.getItemId())
                            .orElseThrow(() -> new ResourceNotFoundException("Project Item", "id", message.getItemId()));
            item.setContent(message.getContent());
            projectItemRepository.save(item);
        }

        message.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/project/" + message.getProjectId(),
                message
        );

    }

    private void validateProjectAccess(Long projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!project.getOwner().getId().equals(user.getId())
                && project.getVisibility() != ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("You don't have access to this project.");
        }

    }

}
