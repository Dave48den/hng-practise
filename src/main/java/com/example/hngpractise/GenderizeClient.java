package com.example.hngpractise;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GenderizeClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Profile classify(String name) {
        String url = "https://api.genderize.io?name=" + name;
        return restTemplate.getForObject(url, Profile.class);
    }
}
