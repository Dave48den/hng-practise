package com.example.hngpractise;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import com.example.hngpractise.User; // Force the correct User

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByGithubId(String githubId);
}
