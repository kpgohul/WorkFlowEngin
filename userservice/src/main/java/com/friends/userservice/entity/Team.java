package com.friends.userservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(
        name = "teams",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_team_name", columnNames = "name")
        }
)
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "team_seq",
        sequenceName = "team_seq",
        initialValue = 1000000000,
        allocationSize = 1
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    @Column(length = 512)
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}


