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

    @PostMapping("/profiles")
    public ResponseEntity<?> createProfile(@RequestParam String name) {
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z]+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Invalid name")
            );
        }
        try {
            Profile profile = profileService.createProfile(name);
            return ResponseEntity.ok(Map.of("status", "success", "data", profile));
        } catch (Exception e) {
            return ResponseEntity.status(502).body(
                    Map.of("status", "error", "message", "Failed to create profile")
            );
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getAllProfiles() {
        List<Profile> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(Map.of("status", "success", "data", profiles));
    }

    @GetMapping("/profile-classify")
    public ResponseEntity<?> classify(@RequestParam String name) {
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z]+")) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Invalid name")
            );
        }

        try {
            Profile profile = profileService.classifyProfile(name);
            return ResponseEntity.ok(Map.of("status", "success", "data", profile));
        } catch (Exception e) {
            return ResponseEntity.status(502).body(
                    Map.of("status", "error", "message", "Genderize returned an invalid response")
            );
        }
    }

}
