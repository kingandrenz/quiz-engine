package com.school.api;

import com.school.model.LeaderboardEntry;
import com.school.model.QuizResult;
import com.school.service.LeaderboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    LeaderboardService leaderboardService;

    private LeaderboardEntry entry(String name, double pct, String category) {
        return LeaderboardEntry.from(
                new QuizResult(name, (int) pct, 100, category, Instant.now()));
    }

    @Test
    @DisplayName("GET /api/leaderboard returns top entries")
    void getLeaderboardReturnsEntries() throws Exception {
        when(leaderboardService.getTopEntries(10))
                .thenReturn(List.of(
                        entry("Alice", 90, "java"),
                        entry("Bob", 80, "java")));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].playerName").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/leaderboard?top=2 respects top param")
    void getLeaderboardRespectsTopParam() throws Exception {
        when(leaderboardService.getTopEntries(2))
                .thenReturn(List.of(entry("Alice", 90, "java")));

        mockMvc.perform(get("/api/leaderboard").param("top", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/leaderboard/category?name=java filters by category")
    void getLeaderboardByCategory() throws Exception {
        when(leaderboardService.getTopEntriesByCategory("java"))
                .thenReturn(List.of(entry("Alice", 90, "java")));

        mockMvc.perform(get("/api/leaderboard/category").param("name", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("java"));
    }
}