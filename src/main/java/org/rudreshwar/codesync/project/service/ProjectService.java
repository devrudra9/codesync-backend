package org.rudreshwar.codesync.project.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.BadRequestException;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.project.dto.CreateProjectRequest;
import org.rudreshwar.codesync.project.dto.ProjectResponse;
import org.rudreshwar.codesync.project.dto.UpdateProjectRequest;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.entity.ProjectStar;
import org.rudreshwar.codesync.project.entity.ProjectVisibility;
import org.rudreshwar.codesync.project.mapper.ProjectMapper;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.project.repository.ProjectStarRepository;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectStarRepository  projectStarRepository;

    public ProjectResponse createProject(CreateProjectRequest request, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .primaryLanguage(request.getPrimaryLanguage())
                .visibility(request.getVisibility() != null ? request.getVisibility() : ProjectVisibility.PRIVATE)
                .archived(false)
                .forkCount(0)
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastOpenedAt(LocalDateTime.now())
                .build();

        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> getMyProjects(String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return projectRepository.findAllByOwner(owner)
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse getProject(Long id, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository
                .findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        return projectMapper.toResponse(project);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository
                .findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (request.getName() != null) {
            project.setName(request.getName());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getPrimaryLanguage() != null) {
            project.setPrimaryLanguage(request.getPrimaryLanguage());
        }

        if (request.getVisibility() != null) {
            project.setVisibility(request.getVisibility());
        }

        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);

        return projectMapper.toResponse(project);
    }

    public void deleteProject(Long id, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository
                .findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        projectRepository.delete(project);
    }

    public List<ProjectResponse> getPublicProjects() {
        return projectRepository
                .findByVisibilityOrderByCreatedAtDesc(ProjectVisibility.PUBLIC)
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public Page<ProjectResponse> searchPublicProjects(String keyword, Pageable pageable) {
        return projectRepository
                .findByVisibilityAndNameContainingIgnoreCase(
                        ProjectVisibility.PUBLIC,
                        keyword,
                        pageable
                )
                .map(projectMapper::toResponse);
    }

    @Transactional
    public ProjectResponse forkProject(Long projectId, String username) {

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project original = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (original.getVisibility() != ProjectVisibility.PUBLIC) {
            throw new BadRequestException("Only public projects can be forked.");
        }

        if (original.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You cannot fork your own project.");
        }

        String baseName = original.getName();
        String forkName = baseName + " (Copy)";
        int copyNumber = 2;

        while (projectRepository.existsByOwnerAndName(currentUser, forkName)) {
            forkName = baseName + " (Copy " + copyNumber + ")";
            copyNumber++;
        }

        LocalDateTime now = LocalDateTime.now();

        Project fork = Project.builder()
                .name(forkName)
                .description(original.getDescription())
                .primaryLanguage(original.getPrimaryLanguage())
                .visibility(ProjectVisibility.PRIVATE)
                .owner(currentUser)
                .parentProject(original)
                .forkCount(0)
                .archived(false)
                .createdAt(now)
                .updatedAt(now)
                .lastOpenedAt(now)
                .build();

        original.setForkCount(original.getForkCount() + 1);

        projectRepository.save(original);
        projectRepository.save(fork);

        return projectMapper.toResponse(fork);
    }

    @Transactional
    public Integer starProject(Long projectId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (project.getVisibility() != ProjectVisibility.PUBLIC) {
            throw new IllegalArgumentException("Only public projects can be starred.");
        }

        if (projectStarRepository.existsByProjectAndUser(project, user)) {
            throw new IllegalArgumentException("Project already starred.");
        }

        ProjectStar star = ProjectStar.builder()
                .project(project)
                .user(user)
                .starredAt(LocalDateTime.now())
                .build();

        projectStarRepository.save(star);

        project.setStarCount(project.getStarCount() + 1);
        projectRepository.save(project);

        return project.getStarCount();
    }

    @Transactional
    public Integer unstarProject(Long projectId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ProjectStar star = projectStarRepository
                .findByProjectAndUser(project, user)
                .orElseThrow(() -> new ResourceNotFoundException("Star not found.", "id", projectId));

        projectStarRepository.delete(star);

        if (project.getStarCount() > 0) {
            project.setStarCount(project.getStarCount() - 1);
        }

        projectRepository.save(project);

        return project.getStarCount();
    }

    @Transactional(readOnly = true)
    public boolean hasStarredProject(Long projectId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        return projectStarRepository.existsByProjectAndUser(project, user);
    }

}