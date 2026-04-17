package com.example.hngpractise;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AgifyClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public AgifyResponse classify(String name) {
        String url = "https://api.agify.io?name=" + name;
        return restTemplate.getForObject(url, AgifyResponse.class);
    }
}
