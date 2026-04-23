package com.example.hngpractise;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    public static Map<String, Object> parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        query = query.toLowerCase();
        Map<String, Object> filters = new HashMap<>();

        // =========================
        // GENDER PARSING
        // =========================
        if (query.contains("male") && query.contains("female")) {
            filters.put("gender", "both"); // handle both genders
        } else if (query.contains("male")) {
            filters.put("gender", "male");
        } else if (query.contains("female")) {
            filters.put("gender", "female");
        }

        // =========================
        // AGE GROUP / RANGE LOGIC
        // =========================
        if (query.contains("child")) {
            filters.put("age_group", "child");
            filters.put("min_age", 0);
            filters.put("max_age", 12);
        }

        if (query.contains("young")) {
            filters.put("min_age", 16);
            filters.put("max_age", 24);
        }

        if (query.contains("teen")) {
            filters.put("age_group", "teenager");
            filters.put("min_age", 13);
            filters.put("max_age", 19);
        }

        if (query.contains("adult")) {
            filters.put("age_group", "adult");
            filters.put("min_age", 18);
            filters.put("max_age", 59);
        }

        if (query.contains("senior") || query.contains("old")) {
            filters.put("age_group", "senior");
            filters.put("min_age", 60);
            filters.put("max_age", 120);
        }

        // =========================
        // AGE CONDITIONS
        // =========================
        if (query.contains("above")) {
            filters.put("min_age", extractNumber(query, "above", 17));
            filters.remove("max_age"); // override
            if (query.contains("teen")) {
                filters.put("age_group", "teenager"); // keep age_group
            }
            if (query.contains("adult")) {
                filters.put("age_group", "adult"); // keep age_group
            }
        }

        if (query.contains("below")) {
            filters.put("max_age", extractNumber(query, "below", 18));
        }

        // =========================
        // COUNTRY PARSING
        // =========================
        if (query.contains("ghana")) {
            filters.put("country_id", "GH");
        }
        if (query.contains("nigeria")) {
            filters.put("country_id", "NG");
        }
        if (query.contains("kenya")) {
            filters.put("country_id", "KE");
        }
        if (query.contains("angola")) {
            filters.put("country_id", "AO");
        }
        if (query.contains("united states") || query.contains("usa")) {
            filters.put("country_id", "US");
        }

        // =========================
        // FINAL VALIDATION
        // =========================
        // Always return filters, even if only one condition matched
        return filters;
    }

    // =========================
    // Helper: extract numbers
    // =========================
    private static int extractNumber(String query, String keyword, int fallback) {
        try {
            String[] words = query.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].equals(keyword) && i + 1 < words.length) {
                    return Integer.parseInt(words[i + 1]);
                }
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }
}
