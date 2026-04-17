package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<?> createProfile(@RequestBody ProfileRequest request) {

        if (request == null || request.getName() == null ||
                request.getName().trim().isEmpty() ||
                !request.getName().matches("[a-zA-Z]+")) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "error",
                            "message", "Invalid name"
                    )
            );
        }

        Profile profile = profileService.createProfile(request.getName());

        return ResponseEntity.status(201).body(
                Map.of(
                        "status", "success",
                        "data", profile
                )
        );
    }

    @GetMapping("/profiles/{id}")
    public ResponseEntity<?> getProfile(@PathVariable UUID id) {

        Profile profile = profileService.getProfileById(id);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "data", profile
                )
        );
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getAllProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) String age_group
    ) {

        List<Profile> profiles = profileService.getAllProfiles();

        if (gender != null && !gender.isEmpty()) {
            profiles = profiles.stream()
                    .filter(p -> p.getGender() != null && p.getGender().equalsIgnoreCase(gender))
                    .collect(Collectors.toList());
        }

        if (country_id != null && !country_id.isEmpty()) {
            profiles = profiles.stream()
                    .filter(p -> p.getCountryId() != null && p.getCountryId().equalsIgnoreCase(country_id))
                    .collect(Collectors.toList());
        }

        if (age_group != null && !age_group.isEmpty()) {
            profiles = profiles.stream()
                    .filter(p -> p.getAgeGroup() != null && p.getAgeGroup().equalsIgnoreCase(age_group))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "count", profiles.size(),
                        "data", profiles
                )
        );
    }

    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable UUID id) {

        profileService.deleteProfile(id);

        return ResponseEntity.noContent().build();
    }
}