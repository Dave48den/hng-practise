package com.example.hngpractise;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.List;

public class NationalizeResponse {

    private String name;

    private List<Country> country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Country> getCountry() {
        return country;
    }

    public void setCountry(List<Country> country) {
        this.country = country;
    }

    // ✅ Safely get the country with the highest probability
    public Country getTopCountry() {
        if (country == null || country.isEmpty()) return null;

        return country.stream()
                .filter(c -> c.getProbability() != null)
                .max(Comparator.comparing(Country::getProbability))
                .orElse(null);
    }

    public static class Country {

        @JsonProperty("country_id")
        private String countryId;

        private Double probability;

        public String getCountryId() {
            return countryId;
        }

        public void setCountryId(String countryId) {
            this.countryId = countryId;
        }

        public Double getProbability() {
            return probability;
        }

        public void setProbability(Double probability) {
            this.probability = probability;
        }
    }
}