package com.example.hngpractise;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "github_id", unique = true, nullable = false)
    private String githubId;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    private String role; // "admin" or "analyst"

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
