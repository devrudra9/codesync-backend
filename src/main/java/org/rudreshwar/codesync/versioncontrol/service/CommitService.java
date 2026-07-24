package org.rudreshwar.codesync.versioncontrol.service;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.common.exception.ResourceNotFoundException;
import org.rudreshwar.codesync.common.util.HashUtil;
import org.rudreshwar.codesync.common.util.ProjectPathUtil;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.repository.ProjectRepository;
import org.rudreshwar.codesync.projectitem.entity.FileType;
import org.rudreshwar.codesync.projectitem.entity.ProjectItem;
import org.rudreshwar.codesync.projectitem.repository.ProjectItemRepository;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.rudreshwar.codesync.versioncontrol.dto.CommitResponse;
import org.rudreshwar.codesync.versioncontrol.dto.CreateCommitRequest;
import org.rudreshwar.codesync.versioncontrol.entity.Commit;
import org.rudreshwar.codesync.versioncontrol.entity.CommitSnapshot;
import org.rudreshwar.codesync.versioncontrol.mapper.CommitMapper;
import org.rudreshwar.codesync.versioncontrol.repository.CommitRepository;
import org.rudreshwar.codesync.versioncontrol.repository.CommitSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommitService {

    private final CommitRepository commitRepository;
    private final CommitSnapshotRepository snapshotRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectItemRepository projectItemRepository;
    private final CommitMapper commitMapper;

    @Transactional
    public CommitResponse createCommit(Long projectId, CreateCommitRequest request, String username) {

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getOwner().getId().equals(author.getId())) {
            throw new IllegalArgumentException("You don't have access to this project.");
        }

        String hash = HashUtil.generateCommitHash(
                project.getId(),
                author.getId(),
                request.getMessage());

        Commit commit = Commit.builder()
                .project(project)
                .author(author)
                .message(request.getMessage())
                .commitHash(hash)
                .committedAt(LocalDateTime.now())
                .build();

        commitRepository.save(commit);

        List<ProjectItem> files = projectItemRepository.findByProjectAndType(project, FileType.FILE);

        for (ProjectItem file : files) {
            CommitSnapshot snapshot = CommitSnapshot.builder()
                    .commit(commit)
                    .path(ProjectPathUtil.buildPath(file))
                    .content(file.getContent())
                    .build();
            snapshotRepository.save(snapshot);
        }

        return commitMapper.toResponse(commit);
    }

    public List<CommitResponse> getCommitHistory(Long projectId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied.");
        }

        return commitRepository
                .findByProjectOrderByCommittedAtDesc(project)
                .stream()
                .map(commitMapper::toResponse)
                .toList();
    }

    @Transactional
    public void restoreCommit(Long projectId, Long commitId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied.");
        }

        Commit commit = commitRepository
                .findByIdAndProject(commitId, project)
                .orElseThrow(() -> new ResourceNotFoundException("Commit", "id", commitId));

        List<CommitSnapshot> snapshots = snapshotRepository.findByCommit(commit);

        for (CommitSnapshot snapshot : snapshots) {
            String filename = snapshot.getPath();
            if (filename.contains("/")) {
                filename = filename.substring(filename.lastIndexOf("/") + 1);
            }

            projectItemRepository.findByProjectAndName(project, filename)
                    .ifPresent(item -> {
                        item.setContent(snapshot.getContent());
                        item.setUpdatedAt(LocalDateTime.now());
                        projectItemRepository.save(item);
                    });
        }

        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

}