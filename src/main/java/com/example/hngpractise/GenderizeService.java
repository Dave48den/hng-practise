package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GenderizeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> classifyName(String name) {

        String cleanName = name.trim().toLowerCase();

        String url = "https://api.genderize.io?name=" + cleanName;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            return Map.of();
        }

        String gender = (String) response.get("gender");

        Double probability = response.get("probability") != null
                ? Double.parseDouble(response.get("probability").toString())
                : 0.0;

        Integer count = response.get("count") != null
                ? Integer.parseInt(response.get("count").toString())
                : 0;

        // ✅ REQUIRED confidence logic (string, not boolean)
        String confidence;
        if (probability >= 0.75) {
            confidence = "high";
        } else if (probability >= 0.5) {
            confidence = "medium";
        } else {
            confidence = "low";
        }

        return Map.of(
                "name", cleanName,
                "gender", gender,
                "probability", probability,
                "count", count,
                "confidence", confidence
        );
    }
}

