package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public Object createProfile(String name) {

        // 1. VALIDATION
        if (name == null || name.trim().isEmpty()) {
            return error("Missing or empty name", 400);
        }

        String cleanName = name.toLowerCase();

        // 2. IDEMPOTENCY CHECK
        Optional<Profile> existing = repository.findByName(cleanName);
        if (existing.isPresent()) {
            return Map.of(
                    "status", "success",
                    "message", "Profile already exists",
                    "data", existing.get()
            );
        }

        try {
            // 3. CALL EXTERNAL APIS
            Map genderData = restTemplate.getForObject(
                    "https://api.genderize.io?name=" + cleanName, Map.class);

            Map ageData = restTemplate.getForObject(
                    "https://api.agify.io?name=" + cleanName, Map.class);

            Map nationalData = restTemplate.getForObject(
                    "https://api.nationalize.io?name=" + cleanName, Map.class);

            // 4. EDGE CASE VALIDATION
            if (genderData == null || genderData.get("gender") == null) {
                return error("Invalid gender data", 502);
            }

            if (ageData == null || ageData.get("age") == null) {
                return error("Invalid age data", 502);
            }

            if (nationalData == null || nationalData.get("country") == null) {
                return error("Invalid country data", 502);
            }

            // 5. EXTRACT DATA
            String gender = (String) genderData.get("gender");
            double genderProbability = ((Number) genderData.get("probability")).doubleValue();
            int sampleSize = ((Number) genderData.get("count")).intValue();

            int age = ((Number) ageData.get("age")).intValue();

            List countries = (List) nationalData.get("country");
            Map topCountry = (Map) countries.get(0);

            String countryId = (String) topCountry.get("country_id");
            double countryProbability = ((Number) topCountry.get("probability")).doubleValue();

            // 6. AGE GROUP LOGIC
            String ageGroup =
                    (age <= 12) ? "child" :
                            (age <= 19) ? "teenager" :
                                    (age <= 59) ? "adult" : "senior";

            // 7. CREATE ENTITY
            Profile profile = new Profile();
            profile.setId(UUID.randomUUID().toString());
            profile.setName(cleanName);
            profile.setGender(gender);
            profile.setGenderProbability(genderProbability);
            profile.setSampleSize(sampleSize);
            profile.setAge(age);
            profile.setAgeGroup(ageGroup);
            profile.setCountryId(countryId);
            profile.setCountryProbability(countryProbability);
            profile.setCreatedAt(Instant.now().toString());

            repository.save(profile);

            // 8. RESPONSE (STRICT FORMAT)
            return Map.of(
                    "status", "success",
                    "data", profile
            );

        } catch (Exception e) {
            return error("External API failure", 502);
        }
    }

    private Map error(String message, int code) {
        return Map.of(
                "status", "error",
                "message", message
        );
    }
}