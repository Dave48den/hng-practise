package com.example.hngpractise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
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
        if (!ALLOWED_SORT_FIELDS.contains(sort_by)) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid query parameters"));
        }

        String mappedSort = sort_by.equals("created_at") ? "createdAt" :
                sort_by.equals("gender_probability") ? "genderProbability" : sort_by;

        int safeLimit = Math.min(limit, 50);
        Sort sort = order.equalsIgnoreCase("desc") ? Sort.by(mappedSort).descending() : Sort.by(mappedSort).ascending();
        PageRequest pageable = PageRequest.of(page - 1, safeLimit, sort);

        Page<Profile> result = profileService.searchProfiles(gender, age_group, country_id, min_age, max_age, min_gender_probability, min_country_probability, pageable);

        PaginatedResponse<Profile> response = PaginatedResponse.<Profile>builder()
                .status("success")
                .page(page)
                .limit(safeLimit)
                .total(result.getTotalElements())
                .total_pages(result.getTotalPages())
                .links(buildLinks("/api/profiles", page, result.getTotalPages(), safeLimit))
                .data(result.getContent())
                .build();

        return ResponseEntity.ok(response);
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
                (String) filters.get("gender"), (String) filters.get("age_group"), (String) filters.get("country_id"),
                (Integer) filters.get("min_age"), (Integer) filters.get("max_age"), null, null, pageable
        );

        PaginatedResponse<Profile> response = PaginatedResponse.<Profile>builder()
                .status("success")
                .page(page)
                .limit(safeLimit)
                .total(result.getTotalElements())
                .total_pages(result.getTotalPages())
                .links(buildLinks("/api/profiles/search", page, result.getTotalPages(), safeLimit))
                .data(result.getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Requirement: POST /api/profiles (Admin Only)
     */
    @PostMapping("/profiles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProfile(@RequestBody Map<String, String> request) {

        String name = request.get("name");

        // Validation
        if (name == null || name.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Name is required");
            return ResponseEntity.status(400).body(error);
        }

        // Process profile (calls external APIs)
        Profile savedProfile = profileService.processAndSaveProfile(name);

        // Safe response (NO Map.of → avoids NullPointerException)
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", savedProfile);

        return ResponseEntity.ok(response);
    }

    /**
     * Requirement: Export Profiles (CSV) with dynamic timestamp and filters
     */
    @GetMapping("/profiles/export")
    public ResponseEntity<String> exportProfiles(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String age_group,
            @RequestParam(required = false) String country_id,
            @RequestParam(defaultValue = "created_at") String sort_by,
            @RequestParam(defaultValue = "desc") String order
    ) {
        // Use a high limit for export or create a service method for all matching records
        List<Profile> profiles = profileService.getAllProfilesForExport(gender, age_group, country_id, sort_by, order);

        StringBuilder csv = new StringBuilder();
        csv.append("id,name,gender,gender_probability,age,age_group,country_id,country_name,country_probability,created_at\n");

        for (Profile p : profiles) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    p.getId(), p.getName(), p.getGender(), p.getGenderProbability(),
                    p.getAge(), p.getAgeGroup(), p.getCountryId(), p.getCountryName(),
                    p.getCountryProbability(), p.getCreatedAt()));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"profiles_" + timestamp + ".csv\"")
                .body(csv.toString());
    }

    private Map<String, String> buildLinks(String baseUri, int currentPage, int totalPages, int limit) {
        Map<String, String> links = new HashMap<>();
        links.put("self", baseUri + "?page=" + currentPage + "&limit=" + limit);
        links.put("next", (currentPage < totalPages) ? baseUri + "?page=" + (currentPage + 1) + "&limit=" + limit : null);
        links.put("prev", (currentPage > 1) ? baseUri + "?page=" + (currentPage - 1) + "&limit=" + limit : null);
        return links;
    }
}
