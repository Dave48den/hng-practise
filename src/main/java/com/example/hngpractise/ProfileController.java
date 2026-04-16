package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Create a new profile
    @PostMapping("/profiles")
    public ResponseEntity<?> createProfile(@RequestParam String name) {
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z]+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Invalid name")
            );
        }

        try {
            Profile profile = profileService.createProfile(name);
            return ResponseEntity.ok(
                    Map.of("status", "success", "data", profile)
            );
        } catch (Exception e) {
            return ResponseEntity.status(502).body(
                    Map.of("status", "error", "message", "Failed to create profile")
            );
        }
    }

    // Get all profiles
    @GetMapping("/profiles")
    public ResponseEntity<?> getAllProfiles() {
        List<Profile> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(
                Map.of("status", "success", "data", profiles)
        );
    }

    // Classify profile by name (gender)
    @GetMapping("/profile-classify")
    public ResponseEntity<?> classify(@RequestParam String name) {
        // Step 1: Validate input
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z]+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Invalid name")
            );
        }

        try {
            // Step 2: Call service
            Profile profile = profileService.createProfile(name);

            // Step 3: Return success
            return ResponseEntity.ok(
                    Map.of("status", "success", "data", profile)
            );
        } catch (Exception e) {
            // Step 4: Catch Genderize failure
            return ResponseEntity.status(502).body(
                    Map.of("status", "error", "message", "Genderize returned an invalid response")
            );
        }
    }
}
