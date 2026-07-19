package org.rudreshwar.codesync.project.repository;

import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOwner(User owner);

    Optional<Project> findByIdAndOwner(Long id, User owner);

}