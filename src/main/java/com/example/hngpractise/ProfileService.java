package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile createProfile(String name) {
        // Idempotency check
        Optional<Profile> existing = profileRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Call external APIs
        GenderizeResponse genderData = restTemplate.getForObject(
                "https://api.genderize.io?name=" + name, GenderizeResponse.class);

        AgifyResponse ageData = restTemplate.getForObject(
                "https://api.agify.io?name=" + name, AgifyResponse.class);

        NationalizeResponse nationalityData = restTemplate.getForObject(
                "https://api.nationalize.io?name=" + name, NationalizeResponse.class);

        // Edge case checks
        if (genderData == null || genderData.getGender() == null || genderData.getCount() == 0) {
            throw new ExternalApiException("Genderize returned an invalid response");
        }
        if (ageData == null || ageData.getAge() == null) {
            throw new ExternalApiException("Agify returned an invalid response");
        }
        if (nationalityData == null || nationalityData.getCountry() == null || nationalityData.getCountry().isEmpty()) {
            throw new ExternalApiException("Nationalize returned an invalid response");
        }

        // Classification logic
        String ageGroup = classifyAge(ageData.getAge());
        NationalizeResponse.Country topCountry = nationalityData.getTopCountry();

        // Build Profile
        Profile profile = new Profile();
        profile.setId(UUID.randomUUID()); // swap with UUID v7 later
        profile.setName(name);
        profile.setGender(genderData.getGender());
        profile.setGenderProbability(genderData.getProbability());
        profile.setSampleSize(genderData.getCount());
        profile.setAge(ageData.getAge());
        profile.setAgeGroup(ageGroup);
        profile.setCountryId(topCountry.getCountry_id());
        profile.setCountryProbability(topCountry.getProbability());
        profile.setCreatedAt(Instant.now());

        // Save to DB
        return profileRepository.save(profile);
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ExternalApiException("Profile not found"));
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public void deleteProfile(UUID id) {
        if (!profileRepository.existsById(id)) {
            throw new ExternalApiException("Profile not found");
        }
        profileRepository.deleteById(id);
    }

    private String classifyAge(int age) {
        if (age <= 12) return "child";
        else if (age <= 19) return "teenager";
        else if (age <= 59) return "adult";
        else return "senior";
    }
}
