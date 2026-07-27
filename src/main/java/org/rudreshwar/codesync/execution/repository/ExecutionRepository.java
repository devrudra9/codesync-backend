package org.rudreshwar.codesync.execution.repository;

import org.rudreshwar.codesync.execution.entity.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRepository extends JpaRepository<Execution,Long> {
}