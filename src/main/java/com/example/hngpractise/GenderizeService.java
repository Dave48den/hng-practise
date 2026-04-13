package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GenderizeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> classifyName(String name) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            String cleanName = name.trim().toLowerCase();
            String url = "https://api.genderize.io?name=" + cleanName;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // ✅ If API fails or returns null
            if (response == null) {
                return Map.of(
                        "name", cleanName,
                        "gender", null
                );
            }

            String gender = (String) response.get("gender");

            // ✅ If gender is null → STOP here (no crash)
            if (gender == null) {
                return Map.of(
                        "name", cleanName,
                        "gender", null
                );
            }

            Double probability = response.get("probability") != null
                    ? Double.parseDouble(response.get("probability").toString())
                    : 0.0;

            Integer count = response.get("count") != null
                    ? Integer.parseInt(response.get("count").toString())
                    : 0;

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

        } catch (Exception e) {
            // ✅ THIS PREVENTS 500 ERRORS COMPLETELY
            return Map.of(
                    "name", name,
                    "gender", null
            );
        }
    }
}

