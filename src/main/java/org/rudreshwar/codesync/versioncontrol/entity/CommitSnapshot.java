package org.rudreshwar.codesync.versioncontrol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commit_snapshots")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commit_id", nullable = false)
    private Commit commit;

    @Column(nullable = false)
    private String path;

    @Column(columnDefinition = "TEXT")
    private String content;
}