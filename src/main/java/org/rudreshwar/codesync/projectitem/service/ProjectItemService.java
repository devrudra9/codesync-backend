package org.rudreshwar.codesync.projectitem.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.projectitem.dto.*;
import org.rudreshwar.codesync.projectitem.entity.FileType;
import org.rudreshwar.codesync.projectitem.entity.ProjectItem;
import org.rudreshwar.codesync.projectitem.mapper.ProjectItemMapper;
import org.rudreshwar.codesync.projectitem.repository.ProjectItemRepository;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectItemService {

    private final ProjectRepository projectRepository;
    private final ProjectItemRepository projectItemRepository;
    private final UserRepository userRepository;
    private final ProjectItemMapper mapper;


    @Transactional
    public ProjectItemResponse createItem(
            Long projectId,
            CreateFileRequest request,
            String username) {

        Project project = getOwnedProject(projectId, username);

        ProjectItem parent = null;

        if (request.getParentId() != null) {

            parent = projectItemRepository
                    .findByIdAndProject(request.getParentId(), project)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Folder",
                                    "id",
                                    request.getParentId()));

            if (parent.getType() != FileType.FOLDER) {
                throw new IllegalArgumentException(
                        "Parent must be a folder.");
            }
        }

        if (projectItemRepository.existsByProjectAndParentAndName(
                project,
                parent,
                request.getName())) {

            throw new IllegalArgumentException(
                    "Item already exists.");
        }

        ProjectItem item = ProjectItem.builder()
                .name(request.getName())
                .type(request.getType())
                .content(request.getType() == FileType.FILE ? "" : null)
                .project(project)
                .parent(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        projectItemRepository.save(item);

        return mapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<ProjectItemTreeResponse> getProjectTree(Long projectId, String username) {

        Project project = getOwnedProject(projectId, username);
        List<ProjectItem> allItems = projectItemRepository.findByProject(project);

        return buildTree(allItems);
    }

    private List<ProjectItemTreeResponse> buildTree(List<ProjectItem> items) {
        Map<Long, ProjectItemTreeResponse> map = new HashMap<>();

        for (ProjectItem item : items) {
            map.put(item.getId(), mapper.toTree(item));
        }

        List<ProjectItemTreeResponse> roots = new ArrayList<>();

        for (ProjectItem item : items) {
            ProjectItemTreeResponse current = map.get(item.getId());

            if (item.getParent() == null) {
                roots.add(current);
            } else {
                map.get(item.getParent().getId())
                        .getChildren()
                        .add(current);
            }
        }

        return roots;
    }

    @Transactional
    public ProjectItemResponse renameItem(Long projectId, Long itemId, RenameFileRequest request, String username) {
        Project project = getOwnedProject(projectId, username);

        ProjectItem item = projectItemRepository
                .findByIdAndProject(itemId, project)
                .orElseThrow(() -> new ResourceNotFoundException("Project Item", "id", itemId));

        if (projectItemRepository.existsByProjectAndParentAndName(project, item.getParent(), request.getName())) {
            throw new IllegalArgumentException("An item with this name already exists.");
        }

        item.setName(request.getName());
        item.setUpdatedAt(LocalDateTime.now());
        projectItemRepository.save(item);

        return mapper.toResponse(item);
    }

    @Transactional
    public ProjectItemResponse updateContent(Long projectId, Long itemId, UpdateFileContentRequest request, String username) {
        Project project = getOwnedProject(projectId, username);

        ProjectItem item = projectItemRepository.findByIdAndProject(itemId, project)
                .orElseThrow(() -> new ResourceNotFoundException("Project Item","id", itemId));

        if (item.getType() != FileType.FILE) {
            throw new IllegalArgumentException("Only files can contain content.");
        }

        item.setContent(request.getContent());
        item.setUpdatedAt(LocalDateTime.now());
        projectItemRepository.save(item);

        return mapper.toResponse(item);
    }

    @Transactional
    public void deleteItem(Long projectId, Long itemId, String username) {
        Project project = getOwnedProject(projectId, username);

        ProjectItem item = projectItemRepository.findByIdAndProject(itemId, project)
                .orElseThrow(() -> new ResourceNotFoundException("Project Item", "id", itemId));

        deleteRecursively(item);
    }



    private Project getOwnedProject(Long projectId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    }


    private void deleteRecursively(ProjectItem item) {
        List<ProjectItem> children = projectItemRepository.findByParent(item);

        for (ProjectItem child : children) {
            deleteRecursively(child);
        }

        projectItemRepository.delete(item);
    }

}
