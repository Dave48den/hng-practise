package com.example.hngpractise;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final GenderizeClient genderizeClient;
    private final AgifyClient agifyClient;
    private final NationalizeClient nationalizeClient;

    public ProfileService(ProfileRepository profileRepository,
                          GenderizeClient genderizeClient,
                          AgifyClient agifyClient,
                          NationalizeClient nationalizeClient) {
        this.profileRepository = profileRepository;
        this.genderizeClient = genderizeClient;
        this.agifyClient = agifyClient;
        this.nationalizeClient = nationalizeClient;
    }

    public Profile createProfile(String name) {

        // Idempotency (do not recreate duplicates)
        Optional<Profile> existing = profileRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setName(name);

        // ALWAYS set timestamp (fixes ISO issue)
        profile.setCreatedAt(Instant.now());

        // -------------------
        // GENDERIZE
        // -------------------
        GenderizeResponse g = genderizeClient.classify(name);
        profile.setGender(g != null && g.getGender() != null ? g.getGender() : "unknown");
        profile.setGenderProbability(g != null ? g.getProbability() : 0.0);
        profile.setSampleSize(g != null ? g.getCount() : 0);

        // -------------------
        // AGIFY
        // -------------------
        AgifyResponse a = agifyClient.classify(name);
        int age = (a != null && a.getAge() != null) ? a.getAge() : 0;

        profile.setAge(age);
        profile.setAgeGroup(getAgeGroup(age));

        // -------------------
        // NATIONALIZE
        // -------------------
        NationalizeResponse n = nationalizeClient.classify(name);

        if (n != null && n.getCountry() != null && !n.getCountry().isEmpty()) {
            NationalizeResponse.Country top = n.getCountry().get(0);

            profile.setCountryId(top.getCountry_id());
            profile.setCountryProbability(top.getProbability());
        } else {
            profile.setCountryId("unknown");
            profile.setCountryProbability(0.0);
        }

        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }

    public void deleteProfile(UUID id) {
        profileRepository.deleteById(id);
    }

    // -------------------
    // AGE GROUP FIX
    // -------------------
    private String getAgeGroup(int age) {
        if (age <= 12) return "child";
        if (age <= 19) return "teenager";
        if (age <= 59) return "adult";
        return "senior";
    }
}