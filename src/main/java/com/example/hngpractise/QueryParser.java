package com.example.hngpractise;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser {
    public static Map<String, Object> parse(String query) {
        if (query == null || query.trim().isEmpty()) return null;

        query = query.toLowerCase();
        Map<String, Object> filters = new HashMap<>();

        // 1. GENDER
        if (query.contains("female")) {
            filters.put("gender", "female");
        } else if (query.contains("male")) {
            filters.put("gender", "male");
        }

        // 2. AGE GROUPS (Hardcoded Keywords)
        if (query.contains("child")) {
            filters.put("age_group", "child");
        } else if (query.contains("teen")) {
            filters.put("age_group", "teenager");
        } else if (query.contains("adult")) {
            filters.put("age_group", "adult");
        } else if (query.contains("senior") || query.contains("old")) {
            filters.put("age_group", "senior");
        }

        // 3. SPECIAL "YOUNG" MAPPING (Requirement: 16-24)
        if (query.contains("young")) {
            filters.put("min_age", 16);
            filters.put("max_age", 24);
        }

        // 4. COMPARISON LOGIC (above/below/over/under)
        Integer extractedNum = extractNumber(query);
        if (extractedNum != null) {
            if (query.contains("above") || query.contains("over") || query.contains("older than")) {
                filters.put("min_age", extractedNum);
            } else if (query.contains("below") || query.contains("under") || query.contains("younger than")) {
                filters.put("max_age", extractedNum);
            }
        }

        // 5. COUNTRIES
        if (query.contains("nigeria")) filters.put("country_id", "NG");
        else if (query.contains("ghana")) filters.put("country_id", "GH");
        else if (query.contains("kenya")) filters.put("country_id", "KE");
        else if (query.contains("angola")) filters.put("country_id", "AO");
        else if (query.contains("usa") || query.contains("united states")) filters.put("country_id", "US");

        return filters.isEmpty() ? null : filters;
    }

    private static Integer extractNumber(String query) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(query);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return null;
    }
}
