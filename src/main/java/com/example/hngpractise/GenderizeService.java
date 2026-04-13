package com.example.hngpractise;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class GenderizeService {

    public Map<String, Object> classifyName(String name) {
        RestTemplate restTemplate = new RestTemplate();

        // Call Genderize API directly
        String url = "https://api.genderize.io?name=" + name;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Extract fields
        String gender = (String) response.get("gender");
        Double probability = response.get("probability") != null
                ? Double.parseDouble(response.get("probability").toString())
                : 0.0;
        Integer sampleSize = response.get("count") != null
                ? Integer.parseInt(response.get("count").toString())
                : 0;

        // Confidence logic
        boolean isConfident = probability >= 0.8 && sampleSize > 100;

        // Build result
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("gender", gender);
        result.put("probability", probability);
        result.put("sample_size", sampleSize);
        result.put("is_confident", isConfident);
        result.put("processed_at", Instant.now().toString());

        return result;
    }
}
