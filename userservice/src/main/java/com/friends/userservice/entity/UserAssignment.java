package com.friends.userservice.entity;

import com.friends.userservice.appconstant.Role;
import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "user_assignments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_assignment_user_id", columnNames = "user_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "user_assignment_seq",
        sequenceName = "user_assignment_seq",
        initialValue = 1000000000,
        allocationSize = 1
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_assignment_seq")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @CreatedDate
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}


