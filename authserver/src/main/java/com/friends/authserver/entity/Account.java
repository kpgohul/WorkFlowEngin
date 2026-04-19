package com.friends.authserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "account_seq", sequenceName = "account_seq", initialValue = 1000000000, allocationSize = 1)
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(name = "uk_account_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_account_email", columnNames = "email")
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private Boolean isExpired = false;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private Boolean isLocked = false;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private Boolean isCredentialsExpired = false;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private Boolean isEnabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities = new LinkedHashSet<>();

    @PrePersist
    @PreUpdate
    private void normalizeAccountStateFlags() {
        if (isExpired == null) {
            isExpired = false;
        }
        if (isLocked == null) {
            isLocked = false;
        }
        if (isCredentialsExpired == null) {
            isCredentialsExpired = false;
        }
        if (isEnabled == null) {
            isEnabled = true;
        }
    }

}