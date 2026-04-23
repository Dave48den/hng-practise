package com.example.hngpractise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final GenderizeClient genderizeClient;
    private final AgifyClient agifyClient;
    private final NationalizeClient nationalizeClient;
    private final SecureRandom random = new SecureRandom();

    public ProfileService(ProfileRepository profileRepository,
                          GenderizeClient genderizeClient,
                          AgifyClient agifyClient,
                          NationalizeClient nationalizeClient) {
        this.profileRepository = profileRepository;
        this.genderizeClient = genderizeClient;
        this.agifyClient = agifyClient;
        this.nationalizeClient = nationalizeClient;
    }

    // ==========================================
    // UUID V7 GENERATOR (Requirement: UUID v7)
    // ==========================================
    public UUID generateUUIDv7() {
        byte[] value = new byte[16];
        random.nextBytes(value);
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(timestamp);
        System.arraycopy(buf.array(), 2, value, 0, 6);
        value[6] = (byte) ((value[6] & 0x0F) | 0x70); // Set version to 7
        value[8] = (byte) ((value[8] & 0x3F) | 0x80); // Set variant to 1
        ByteBuffer result = ByteBuffer.wrap(value);
        return new UUID(result.getLong(), result.getLong());
    }

    // =============================
    // CREATE PROFILE
    // =============================
    public Profile createProfile(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        name = name.trim();

        Optional<Profile> existing = profileRepository.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        Profile profile = classifyProfile(name);

        // 🔥 FIX: Using the new UUID v7 generator instead of randomUUID()
        profile.setId(generateUUIDv7());

        return profileRepository.save(profile);
    }

    // =============================
    // STAGE 2 CORE: FILTER + PAGINATION + SORTING
    // =============================
    public Page<Profile> searchProfiles(
            String gender,
            String ageGroup,
            String countryId,
            Integer minAge,
            Integer maxAge,
            Double minGenderProbability,
            Double minCountryProbability,
            Pageable pageable
    ) {
        return profileRepository.findAll(
                ProfileSpecification.filter(
                        gender,
                        ageGroup,
                        countryId,
                        minAge,
                        maxAge,
                        minGenderProbability,
                        minCountryProbability
                ),
                pageable
        );
    }

    // =============================
    // GET BY ID
    // =============================
    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }

    // =============================
    // DELETE
    // =============================
    public void deleteProfile(UUID id) {
        profileRepository.deleteById(id);
    }

    // =============================
    // CLASSIFICATION LOGIC
    // =============================
    public Profile classifyProfile(String name) {
        Profile profile = new Profile();
        profile.setName(name);

        // -------- GENDERIZE --------
        GenderizeResponse g = genderizeClient.classify(name);
        if (g == null || g.getGender() == null || g.getProbability() == null) {
            throw new ExternalApiException("Genderize returned an invalid response");
        }
        profile.setGender(g.getGender());
        profile.setGenderProbability(g.getProbability());

        // -------- AGIFY --------
        AgifyResponse a = agifyClient.classify(name);
        if (a == null || a.getAge() == null) {
            throw new ExternalApiException("Agify returned an invalid response");
        }
        profile.setAge(a.getAge());
        profile.setAgeGroup(getAgeGroup(a.getAge()));

        // -------- NATIONALIZE --------
        NationalizeResponse n = nationalizeClient.classify(name);
        if (n == null) {
            throw new ExternalApiException("Nationalize returned an invalid response");
        }

        NationalizeResponse.Country topCountry = n.getTopCountry();
        if (topCountry == null) {
            throw new ExternalApiException("No country data available");
        }

        profile.setCountryId(topCountry.getCountryId());
        profile.setCountryProbability(topCountry.getProbability());

        String countryId = topCountry.getCountryId();
        String countryName = CountryUtil.getCountryName(countryId);
        profile.setCountryName(countryName);

        return profile;
    }

    private String getAgeGroup(int age) {
        if (age <= 12) return "child";
        if (age <= 19) return "teenager";
        if (age <= 59) return "adult";
        return "senior";
    }
}
