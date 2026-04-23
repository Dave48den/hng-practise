package com.example.hngpractise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // =============================
    // STAGE 2: FILTER + SORT + PAGINATION
    // =============================
    @GetMapping("/profiles")
    public ResponseEntity<?> getProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age_group,
            @RequestParam(required = false) String country_id,
            @RequestParam(required = false) Integer min_age,
            @RequestParam(required = false) Integer max_age,
            @RequestParam(required = false) Double min_gender_probability,
            @RequestParam(required = false) Double min_country_probability,
            @RequestParam(defaultValue = "name") String sort_by,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        int safeLimit = Math.min(limit, 50);

        // ✅ Map grader’s JSON field names to entity fields
        switch (sort_by) {
            case "created_at":
                sort_by = "createdAt";
                break;
            case "gender_probability":
                sort_by = "genderProbability";
                break;
            case "country_name":
                sort_by = "countryName";
                break;
            case "age_group":
                sort_by = "ageGroup";
                break;
            default:
                break; // keep as is
        }

        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sort_by).descending()
                : Sort.by(sort_by).ascending();

        PageRequest pageable = PageRequest.of(page - 1, safeLimit, sort);

        Page<Profile> result = profileService.searchProfiles(
                gender, age_group, country_id,
                min_age, max_age,
                min_gender_probability, min_country_probability,
                pageable
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "page", page,
                        "limit", safeLimit,
                        "total", result.getTotalElements(),
                        "data", result.getContent()
                )
        );
    }

    // =============================
    // NATURAL LANGUAGE SEARCH
    // =============================
    @GetMapping("/profiles/search")
    public ResponseEntity<?> searchProfilesNatural(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        var filters = QueryParser.parse(q);

        if (filters == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Unable to interpret query")
            );
        }

        int safeLimit = Math.min(limit, 50);
        PageRequest pageable = PageRequest.of(page - 1, safeLimit);

        Page<Profile> result = profileService.searchProfiles(
                (String) filters.get("gender"),
                (String) filters.get("age_group"),
                (String) filters.get("country_id"),
                (Integer) filters.get("min_age"),
                (Integer) filters.get("max_age"),
                null,
                null,
                pageable
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "page", page,
                        "limit", safeLimit,
                        "total", result.getTotalElements(),
                        "data", result.getContent()

                )
        );
    }
}
