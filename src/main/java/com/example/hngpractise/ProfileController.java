package com.example.hngpractise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProfileController {
    private final ProfileService profileService;
    private final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("age", "created_at", "gender_probability", "name");

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profiles")
    public ResponseEntity<?> getProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age_group,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) Integer min_age,
            @RequestParam(required = false) Integer max_age,
            @RequestParam(required = false) Double min_gender_probability,
            @RequestParam(required = false) Double min_country_probability,
            @RequestParam(defaultValue = "created_at") String sort_by,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // VALIDATION: Prevent 500 error on invalid sort field
        if (!ALLOWED_SORT_FIELDS.contains(sort_by)) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid query parameters"));
        }

        // Map camelCase for JPA
        String mappedSort = sort_by;
        if (sort_by.equals("created_at")) mappedSort = "createdAt";
        if (sort_by.equals("gender_probability")) mappedSort = "genderProbability";

        int safeLimit = Math.min(limit, 50);
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(mappedSort).descending() : Sort.by(mappedSort).ascending();
        PageRequest pageable = PageRequest.of(page - 1, safeLimit, sort);

        Page<Profile> result = profileService.searchProfiles(gender, age_group, country_id, min_age, max_age, min_gender_probability, min_country_probability, pageable);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "page", page,
                "limit", safeLimit,
                "total", result.getTotalElements(),
                "data", result.getContent()
        ));
    }

    @GetMapping("/profiles/search")
    public ResponseEntity<?> searchProfilesNatural(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var filters = QueryParser.parse(q);
        if (filters == null) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Unable to interpret query"));
        }

        int safeLimit = Math.min(limit, 50);
        PageRequest pageable = PageRequest.of(page - 1, safeLimit);

        Page<Profile> result = profileService.searchProfiles(
                (String) filters.get("gender"),
                (String) filters.get("age_group"),
                (String) filters.get("country_id"),
                (Integer) filters.get("min_age"),
                (Integer) filters.get("max_age"),
                null, null, pageable
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "page", page,
                "limit", safeLimit,
                "total", result.getTotalElements(),
                "data", result.getContent()
        ));
    }
}
