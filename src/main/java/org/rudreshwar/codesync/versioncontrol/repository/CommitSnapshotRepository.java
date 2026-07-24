package org.rudreshwar.codesync.versioncontrol.repository;

import org.rudreshwar.codesync.versioncontrol.entity.Commit;
import org.rudreshwar.codesync.versioncontrol.entity.CommitSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommitSnapshotRepository extends JpaRepository<CommitSnapshot, Long> {

    List<CommitSnapshot> findByCommit(Commit commit);

}