package org.rudreshwar.codesync.versioncontrol.repository;

import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.versioncontrol.entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommitRepository extends JpaRepository<Commit, Long> {

    List<Commit> findByProjectOrderByCommittedAtDesc(Project project);

    Optional<Commit> findByIdAndProject(Long id, Project project);

}