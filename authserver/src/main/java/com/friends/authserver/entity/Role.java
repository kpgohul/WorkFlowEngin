package com.friends.authserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SequenceGenerator(
        name = "role_seq",
        sequenceName = "role_seq",
        initialValue = 1000000000,
        allocationSize = 1
)
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(name = "uk_role_name", columnNames = "name"))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "authorities", targetEntity = Account.class)
    private Set<Account> accounts = new LinkedHashSet<>();
}