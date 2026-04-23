package com.example.hngpractise;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    public static Map<String, Object> parse(String query) {

        query = query.toLowerCase();

        Map<String, Object> filters = new HashMap<>();

        // GENDER
        if (query.contains("male")) {
            filters.put("gender", "male");
        }
        if (query.contains("female")) {
            filters.put("gender", "female");
        }

        // AGE GROUP
        if (query.contains("child")) {
            filters.put("age_group", "child");
        } else if (query.contains("teen")) {
            filters.put("age_group", "teenager");
        } else if (query.contains("adult")) {
            filters.put("age_group", "adult");
        } else if (query.contains("senior")) {
            filters.put("age_group", "senior");
        }

        // "young" special rule (16–24)
        if (query.contains("young")) {
            filters.put("min_age", 16);
            filters.put("max_age", 24);
        }

        // AGE CONDITIONS
        if (query.contains("above 30")) {
            filters.put("min_age", 30);
        }

        // COUNTRY (basic mapping)
        if (query.contains("nigeria")) {
            filters.put("country_id", "NG");
        } else if (query.contains("ghana")) {
            filters.put("country_id", "GH");
        } else if (query.contains("kenya")) {
            filters.put("country_id", "KE");
        } else if (query.contains("angola")) {
            filters.put("country_id", "AO");
        }

        return filters;
    }
}