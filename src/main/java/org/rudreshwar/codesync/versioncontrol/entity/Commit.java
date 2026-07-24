package org.rudreshwar.codesync.versioncontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import org.rudreshwar.codesync.project.entity.Project;
import org.rudreshwar.codesync.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "commits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    private String commitHash;

    @Column(nullable = false, length = 200)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private LocalDateTime committedAt;
}