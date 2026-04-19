package com.friends.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users", uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_account_id", columnNames = "account_id"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_username", columnNames = "username")
})
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 1000000000, allocationSize = 1)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
        private Long id;

        @Column(name = "account_id", nullable = false, unique = true)
        private Long accountId;

        @Column(nullable = false, unique = true, length = 64)
        private String username;

        @Column(nullable = false, unique = true, length = 128)
        private String email;

        @Column(length = 16)
        private String gender;

        private LocalDate dateOfBirth;

        @Column(length = 8)
        private String bloodGroup;

        private Integer age;

        @Column(length = 8)
        private String countryCode;

        @Column(length = 20)
        private String mobile;

        @Column(length = 64)
        private String country;

        @Column(length = 64)
        private String state;

        @Column(length = 256)
        private String address;

        @Column(length = 16)
        private String pincode;

        @Builder.Default
        @Column(nullable = false)
        private Boolean isActive = true;

        @CreatedDate
        @Column(nullable = false, updatable = false)
        private Instant createdAt;

        @LastModifiedDate
        @Column(nullable = false)
        private Instant updatedAt;
}
