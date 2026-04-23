package com.example.hngpractise;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String gender;

    @Column(name = "gender_probability")
    @JsonProperty("gender_probability")
    private Double genderProbability;

    private Integer age;

    @Column(name = "age_group")
    @JsonProperty("age_group")
    private String ageGroup;

    @Column(name = "country_id", length = 2)
    @JsonProperty("country_id")
    private String countryId;

    @Column(name = "country_name")
    @JsonProperty("country_name")
    private String countryName;

    @Column(name = "country_probability")
    @JsonProperty("country_probability")
    private Double countryProbability;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    public Profile() {
    }

    // Add this setter so the seeder or JPA doesn't complain
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // ... Keep all your existing getters and setters below ...
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getGenderProbability() {
        return genderProbability;
    }

    public void setGenderProbability(Double genderProbability) {
        this.genderProbability = genderProbability;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Double getCountryProbability() {
        return countryProbability;
    }

    public void setCountryProbability(Double countryProbability) {
        this.countryProbability = countryProbability;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
