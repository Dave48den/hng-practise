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
        // Idempotency
        Optional<Profile> existing = profileRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        Profile profile = classifyProfile(name);
        profile.setId(UUID.randomUUID());
        profile.setCreatedAt(Instant.now());
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

    public Profile classifyProfile(String name) {
        Profile profile = new Profile();
        profile.setName(name);

        // -------------------
        // GENDERIZE
        // -------------------
        GenderizeResponse g = genderizeClient.classify(name);
        if (g == null || g.getGender() == null || g.getCount() == 0) {
            throw new ExternalApiException("Genderize returned an invalid response");
        }
        profile.setGender(g.getGender());
        profile.setGenderProbability(g.getProbability());
        profile.setSampleSize(g.getCount());

        // -------------------
        // AGIFY
        // -------------------
        AgifyResponse a = agifyClient.classify(name);
        if (a == null || a.getAge() == null) {
            throw new ExternalApiException("Agify returned an invalid response");
        }
        profile.setAge(a.getAge());
        profile.setAgeGroup(getAgeGroup(a.getAge()));

        // -------------------
        // NATIONALIZE
        // -------------------
        NationalizeResponse n = nationalizeClient.classify(name);
        if (n == null || n.getTopCountry() == null) {
            throw new ExternalApiException("Nationalize returned an invalid response");
        }
        profile.setCountryId(n.getTopCountry().getCountry_id());
        profile.setCountryProbability(n.getTopCountry().getProbability());

        return profile;
    }

    private String getAgeGroup(int age) {
        if (age <= 12) return "child";
        if (age <= 19) return "teenager";
        if (age <= 59) return "adult";
        return "senior";
    }
}
