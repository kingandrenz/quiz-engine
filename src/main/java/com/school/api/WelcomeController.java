package com.school.api;

import com.school.api.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, String>>> welcome() {
        Map<String, String> endpoints = Map.of(
            "start quiz",              "/api/quiz/start?category=java&limit=5",
            "all categories",          "/api/quiz/categories",
            "submit answers",          "/api/quiz/submit (POST)",
            "leaderboard",             "/api/leaderboard",
            "leaderboard by category", "/api/leaderboard/category?name=java"
        );
        return ResponseEntity.ok(ApiResponse.ok("Quiz Engine API is running", endpoints));
    }
}
