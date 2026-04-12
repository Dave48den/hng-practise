package com.example.hngpractise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")   // Base path for all endpoints in this controller
public class ClassifyController {

    private final GenderizeService genderizeService;

    // Constructor injection: Spring automatically provides the service
    public ClassifyController(GenderizeService genderizeService) {
        this.genderizeService = genderizeService;
    }

    @GetMapping("/classify")   // Defines GET /api/classify
    public ResponseEntity<?> classify(@RequestParam(required = false) String name) {
        // Error: missing or empty name
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "error", "message", "Missing or empty name parameter")
            );
        }

        try {
            // Delegate to the service
            return ResponseEntity.ok(genderizeService.classifyName(name));
        } catch (Exception e) {
            // Error: upstream or server failure
            return ResponseEntity.status(502).body(
                    Map.of("status", "error", "message", "Upstream or server failure")
            );
        }
    }
}
