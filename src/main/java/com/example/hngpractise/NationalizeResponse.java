package com.example.hngpractise;

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

    public Country getTopCountry() {
        if (country == null || country.isEmpty()) return null;
        return country.stream()
                .max((c1, c2) -> Double.compare(c1.getProbability(), c2.getProbability()))
                .orElse(null);
    }

    public static class Country {
        private String country_id;
        private Double probability;

        public String getCountry_id() {
            return country_id;
        }

        public void setCountry_id(String country_id) {
            this.country_id = country_id;
        }

        public Double getProbability() {
            return probability;
        }

        public void setProbability(Double probability) {
            this.probability = probability;
        }
    }
}
