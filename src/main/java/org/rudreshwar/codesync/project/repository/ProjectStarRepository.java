package org.rudreshwar.codesync.project.repository;

import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.project.entity.ProjectStar;
import org.rudreshwar.codesync.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectStarRepository extends JpaRepository<ProjectStar, Long> {

    boolean existsByProjectAndUser(Project project, User user);

    Optional<ProjectStar> findByProjectAndUser(Project project, User user);

    long countByProject(Project project);
}