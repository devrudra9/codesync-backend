package org.rudreshwar.codesync.project.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.project.dto.CreateProjectRequest;
import org.rudreshwar.codesync.project.dto.ProjectResponse;
import org.rudreshwar.codesync.project.dto.UpdateProjectRequest;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.entity.Visibility;
import org.rudreshwar.codesync.project.mapper.ProjectMapper;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponse createProject(CreateProjectRequest request, String username) {

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .primaryLanguage(request.getPrimaryLanguage())
                .visibility(Visibility.PRIVATE)
                .archived(false)
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

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setPrimaryLanguage(request.getPrimaryLanguage());
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

}