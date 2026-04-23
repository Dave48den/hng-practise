package com.example.hngpractise;

import java.util.Map;

public class CountryUtil {

    private static final Map<String, String> COUNTRY_MAP = Map.of(
            "GH", "Ghana",
            "NG", "Nigeria",
            "US", "United States",
            "GB", "United Kingdom",
            "CA", "Canada",
            "KE", "Kenya"
    );

    public static String getCountryName(String countryId) {
        if (countryId == null) return "Unknown";
        return COUNTRY_MAP.getOrDefault(countryId, "Unknown");
    }
}