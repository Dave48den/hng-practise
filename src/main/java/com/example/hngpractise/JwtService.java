package com.example.hngpractise;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String SECRET_KEY = "insighta_labs_plus_secure_backend_key_2024_stage_3";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private final long ACCESS_TOKEN_EXPIRY = 180000; // 3 mins
    private final long REFRESH_TOKEN_EXPIRY = 300000; // 5 mins

    private final RefreshTokenRepository refreshTokenRepository;


    public JwtService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateRefreshToken(User user) {

        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(user.getId())
                .isValid(true)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }


    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }


    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }


    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}