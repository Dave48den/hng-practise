package com.example.hngpractise;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    private String gender;
    private Double genderProbability;

    private Integer sampleSize;

    private Integer age;
    private String ageGroup;

    private String countryId;
    private Double countryProbability;

    private String createdAt; // UTC ISO 8601

    public Profile() {
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
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

    public Double getCountryProbability() {
        return countryProbability;
    }

    public void setCountryProbability(Double countryProbability) {
        this.countryProbability = countryProbability;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
