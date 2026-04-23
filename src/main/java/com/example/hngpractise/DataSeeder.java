package com.example.hngpractise;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    public DataSeeder(ProfileRepository profileRepository, ProfileService profileService) {
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @Override
    public void run(String... args) {
        try {
            if (profileRepository.count() == 0) {
                System.out.println("🚀 Starting Manual Regex Seeding...");

                String content = new BufferedReader(new InputStreamReader(
                        new ClassPathResource("data/profiles.json").getInputStream()))
                        .lines().collect(Collectors.joining(" "));

                // Split by profile objects
                String[] records = content.split("\\},");

                for (String record : records) {
                    Profile p = new Profile();
                    p.setId(profileService.generateUUIDv7());

                    p.setName(getValue(record, "name"));
                    p.setGender(getValue(record, "gender"));
                    p.setAgeGroup(getValue(record, "age_group"));
                    p.setCountryId(getValue(record, "country_id"));
                    p.setCountryName(getValue(record, "country_name"));

                    try {
                        p.setAge(Integer.parseInt(getValue(record, "age")));
                        p.setGenderProbability(Double.parseDouble(getValue(record, "gender_probability")));
                        p.setCountryProbability(Double.parseDouble(getValue(record, "country_probability")));
                    } catch (Exception e) {
                        // Use defaults if numbers are missing
                        if (p.getAge() == null) p.setAge(0);
                    }

                    profileRepository.save(p);
                }
                System.out.println("✅ Seeding Successful with " + records.length + " records!");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Seeder error: " + e.getMessage());
        }
    }

    private String getValue(String record, String key) {
        // Regex to find "key": "value" or "key": 123
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
        Matcher matcher = pattern.matcher(record);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "0";
    }
}
