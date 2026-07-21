package org.rudreshwar.codesync.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.rudreshwar.codesync.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_stars",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"project_id", "user_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime starredAt;
}