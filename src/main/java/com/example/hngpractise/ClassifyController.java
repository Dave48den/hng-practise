package com.example.hngpractise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // ✅ Fixes CORS issue
public class ClassifyController {

    @Autowired
    private GenderizeService genderizeService;

    @GetMapping("/classify")
    public ResponseEntity<Map<String, Object>> classify(@RequestParam(required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name parameter is required");
        }

        Map<String, Object> data = genderizeService.classifyName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
