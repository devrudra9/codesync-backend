package org.rudreshwar.codesync.projectitem.repository;

import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.projectitem.entity.ProjectItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, Long> {

    List<ProjectItem> findByProject(Project project);

    List<ProjectItem> findByParent(ProjectItem parent);

    List<ProjectItem> findByProjectAndParentIsNull(Project project);

    Optional<ProjectItem> findByIdAndProject(Long id, Project project);

    boolean existsByProjectAndParentAndName(Project project, ProjectItem parent, String name);
}