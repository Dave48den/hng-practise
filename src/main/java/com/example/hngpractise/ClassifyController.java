package com.example.hngpractise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // ✅ Fixes CORS issue
public class ClassifyController {

    @Autowired
    private GenderizeService genderizeService;

    @GetMapping("/classify")
    public ResponseEntity<?> classify(@RequestParam(required = false) String name) {

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Name parameter is required"
            ));
        }

        Map<String, Object> data = genderizeService.classifyName(name.trim());

        // Handle unknown / nonsense names
        if (data.get("gender") == null) {
            return ResponseEntity.status(422).body(Map.of(
                    "error", "Unable to determine gender"
            ));
        }

        return ResponseEntity.ok(data); // ✅ return directly (NO wrapper)
    }
}
