package com.example.hngpractise;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubUser getUserFromGithub(String code) {

        // =========================
        // STEP 1: Get access token
        // =========================
        String tokenUrl = "https://github.com/login/oauth/access_token";

        Map<String, String> params = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(params, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token: " + response.getBody());
        }

        String token = (String) response.getBody().get("access_token");

        // =========================
        // STEP 2: Get user profile
        // =========================
        String userUrl = "https://api.github.com/user";

        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(token);

        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);

        GitHubUser githubUser = restTemplate.exchange(
                userUrl,
                HttpMethod.GET,
                userRequest,
                GitHubUser.class
        ).getBody();

        // =========================
        // STEP 3: Get email (FIX)
        // =========================
        String emailUrl = "https://api.github.com/user/emails";

        HttpEntity<String> emailRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<List> emailResponse =
                restTemplate.exchange(emailUrl, HttpMethod.GET, emailRequest, List.class);

        if (emailResponse.getBody() != null) {
            for (Object obj : emailResponse.getBody()) {
                Map map = (Map) obj;

                if (Boolean.TRUE.equals(map.get("primary"))) {
                    githubUser.setEmail((String) map.get("email"));
                    break;
                }
            }
        }

        return githubUser;
    }
}