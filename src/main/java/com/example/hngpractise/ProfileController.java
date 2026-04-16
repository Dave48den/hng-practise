package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Create profile (POST /api/profiles?name=John)
    @PostMapping("/profiles")
    public ResponseEntity<Profile> createProfile(@RequestParam String name) {
        Profile profile = profileService.createProfile(name);
        return ResponseEntity.ok(profile);
    }

    // Get all profiles
    @GetMapping("/profiles")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    // Classify profile with validation
    @GetMapping("/profile-classify")
    public ResponseEntity<?> classify(@RequestParam String name) {
        // validation: reject empty or nonsense input
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z]+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Invalid name")
            );
        }

        // create and classify profile
        Profile profile = profileService.createProfile(name);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "data", profile
                )
        );
    }

    // Get profile by ID
    @GetMapping("/profiles/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    // Delete profile by ID
    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
