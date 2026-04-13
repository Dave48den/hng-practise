package com.example.hngpractise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassifyController {

    @Autowired
    private GenderizeService genderizeService;

    @GetMapping("/classify")
    public ResponseEntity<Map<String, Object>> classify(@RequestParam(required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Name parameter is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Map<String, Object> data = genderizeService.classifyName(name.trim());

        if (data == null || data.get("gender") == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unable to determine gender");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
