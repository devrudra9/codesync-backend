package org.rudreshwar.codesync.versioncontrol.mapper;

import org.rudreshwar.codesync.versioncontrol.dto.CommitResponse;
import org.rudreshwar.codesync.versioncontrol.entity.Commit;
import org.springframework.stereotype.Component;

@Component
public class CommitMapper {

    public CommitResponse toResponse(Commit commit) {

        return CommitResponse.builder()
                .id(commit.getId())
                .commitHash(commit.getCommitHash())
                .message(commit.getMessage())
                .author(commit.getAuthor().getUsername())
                .committedAt(commit.getCommittedAt())
                .build();
    }
}