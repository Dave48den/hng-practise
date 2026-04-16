package com.example.hngpractise;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final GenderizeClient genderizeClient;

    public ProfileService(ProfileRepository profileRepository, GenderizeClient genderizeClient) {
        this.profileRepository = profileRepository;
        this.genderizeClient = genderizeClient;
    }

    public Profile createProfile(String name) {
        Profile profile = classifyProfile(name);
        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile classifyProfile(String name) {
        try {
            Profile profile = genderizeClient.classify(name);

            // Fallback: if probability is too low, mark as unknown
            if (profile.getGenderProbability() == null || profile.getGenderProbability() < 0.5) {
                profile.setGender("unknown");
            }

            return profile;
        } catch (Exception e) {
            // Fallback if Genderize completely fails
            Profile fallback = new Profile();
            fallback.setName(name);
            fallback.setGender("unknown");
            return fallback;
        }
    }

}
