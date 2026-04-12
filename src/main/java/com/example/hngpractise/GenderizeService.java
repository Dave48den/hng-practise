package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.Map;

@Service
public class GenderizeService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> classifyName(String name) {
        String url = "https://api.genderize.io?name=" + name;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            return Map.of("status", "error", "message", "Upstream or server failure");
        }

        String gender = (String) response.get("gender");

        int sampleSize = response.get("count") == null ? 0 :
                Integer.parseInt(response.get("count").toString());

        double probability = response.get("probability") == null ? 0.0 :
                Double.parseDouble(response.get("probability").toString());

        // ✅ Edge case check
        if (gender == null || sampleSize == 0) {
            return Map.of(
                    "status", "error",
                    "message", "No prediction available for the provided name"
            );
        }

        boolean isConfident = probability >= 0.7 && sampleSize >= 100;

        return Map.of(
                "status", "success",
                "data", Map.of(
                        "name", name.toLowerCase(),
                        "gender", gender,
                        "probability", probability,
                        "sample_size", sampleSize,
                        "is_confident", isConfident,
                        "processed_at", Instant.now().toString()
                )
        );
    }
}
