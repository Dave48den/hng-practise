package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping("/profiles")
    public ResponseEntity<?> createProfile(@RequestBody ProfileRequest request) {
        return ResponseEntity.ok(service.createProfile(request.getName()));
    }
}