package com.school.api;

import com.school.api.dto.ApiResponse;
import com.school.model.LeaderboardEntry;
import com.school.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // GET /api/leaderboard
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getLeaderboard(
            @RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(
                ApiResponse.ok(leaderboardService.getTopEntries(top)));
    }

    // GET /api/leaderboard/category?name=java
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getByCategory(
            @RequestParam String name) {
        return ResponseEntity.ok(
                ApiResponse.ok(leaderboardService.getTopEntriesByCategory(name)));
    }
}