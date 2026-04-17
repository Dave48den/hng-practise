package com.example.hngpractise;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NationalizeClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public NationalizeResponse classify(String name) {
        String url = "https://api.nationalize.io?name=" + name;
        return restTemplate.getForObject(url, NationalizeResponse.class);
    }
}
