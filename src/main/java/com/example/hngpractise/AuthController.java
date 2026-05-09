package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final GitHubService githubService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(JwtService jwtService,
                          UserRepository userRepository,
                          GitHubService githubService,
                          RefreshTokenRepository refreshTokenRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.githubService = githubService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping("/github/callback")
    public ResponseEntity<?> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state
    ) {

        if (code == null || code.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Missing authorization code"
                    )
            );
        }

        if (state == null || state.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Missing state parameter"
                    )
            );
        }

        try {

            GitHubUser githubUser = githubService.getUserFromGithub(code);

            if (githubUser == null || githubUser.getId() == null) {

                return ResponseEntity.status(401).body(
                        Map.of(
                                "status", "error",
                                "message", "Invalid GitHub authentication"
                        )
                );
            }

            User user = userRepository.findByGithubId(githubUser.getId())
                    .orElseGet(() -> {

                        User newUser = new User();

                        newUser.setGithubId(githubUser.getId());
                        newUser.setUsername(githubUser.getLogin());

                        newUser.setEmail(
                                githubUser.getEmail() != null
                                        ? githubUser.getEmail()
                                        : "no-email@github.com"
                        );

                        newUser.setAvatarUrl(githubUser.getAvatar_url());

                        if ("Dave48den".equalsIgnoreCase(githubUser.getLogin())) {

                            newUser.setRole("ADMIN");

                        } else {

                            newUser.setRole("ANALYST");
                        }

                        return userRepository.save(newUser);
                    });

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return ResponseEntity.ok(
                    Map.of(
                            "status", "success",
                            "access_token", accessToken,
                            "refresh_token", refreshToken,
                            "role", user.getRole()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", "error",
                            "message", "Authentication failed"
                    )
            );
        }
    }
    @GetMapping("/github")
    public void redirectToGithub(HttpServletResponse response) throws IOException {

        String githubUrl =
                "https://github.com/login/oauth/authorize" +
                        "?client_id=Ov23libTKVOGGXySyEwe" +
                        "&scope=user:email";

        response.sendRedirect(githubUrl);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refresh_token");

        if (refreshToken == null || refreshToken.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Refresh token is required"
                    )
            );
        }

        RefreshToken storedToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElse(null);

        if (storedToken == null) {

            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", "error",
                            "message", "Invalid refresh token"
                    )
            );
        }

        if (!storedToken.isValid()) {

            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", "error",
                            "message", "Token already used"
                    )
            );
        }

        if (!jwtService.isTokenValid(refreshToken)) {

            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", "error",
                            "message", "Token expired"
                    )
            );
        }

        storedToken.setValid(false);

        refreshTokenRepository.save(storedToken);

        String userId = jwtService.extractUserId(refreshToken);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElse(null);

        if (user == null) {

            return ResponseEntity.status(404).body(
                    Map.of(
                            "status", "error",
                            "message", "User not found"
                    )
            );
        }

        String newAccessToken =
                jwtService.generateAccessToken(user);

        String newRefreshToken =
                jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "access_token", newAccessToken,
                        "refresh_token", newRefreshToken
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {

        String refreshToken = body.get("refresh_token");

        if (refreshToken == null || refreshToken.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Refresh token is required"
                    )
            );
        }

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElse(null);

        if (token == null) {

            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", "error",
                            "message", "Invalid refresh token"
                    )
            );
        }

        token.setValid(false);

        refreshTokenRepository.save(token);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Logged out successfully"
                )
        );
    }
}