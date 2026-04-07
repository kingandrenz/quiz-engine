package com.school.service;

import com.school.model.LeaderboardEntry;
import com.school.model.QuizResult;
import com.school.repository.LeaderboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    LeaderboardRepository leaderboardRepository;

    LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        leaderboardService = new LeaderboardService(leaderboardRepository);
    }

    private LeaderboardEntry entry(String name, double pct, String category) {
        return LeaderboardEntry.from(
                new QuizResult(name, (int) pct, 100, category, Instant.now()));
    }

    @Test
    @DisplayName("recordResult saves entry to repository")
    void recordResultSavesEntry() {
        QuizResult result = new QuizResult("Alice", 8, 10, "java", Instant.now());
        leaderboardService.recordResult(result);
        verify(leaderboardRepository, times(1)).save(any(LeaderboardEntry.class));
    }

    @Test
    @DisplayName("getTopEntries returns default top 10")
    void getTopEntriesDefault() {
        when(leaderboardRepository.getTopN(10)).thenReturn(List.of());
        leaderboardService.getTopEntries();
        verify(leaderboardRepository).getTopN(10);
    }

    @Test
    @DisplayName("getTopEntries(n) returns correct count")
    void getTopEntriesCustomN() {
        when(leaderboardRepository.getTopN(3)).thenReturn(List.of(
                entry("Alice", 90, "java"),
                entry("Bob", 80, "java"),
                entry("Carol", 70, "java")));
        List<LeaderboardEntry> result = leaderboardService.getTopEntries(3);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("getTopEntries(0) throws IllegalArgumentException")
    void getTopEntriesZeroThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> leaderboardService.getTopEntries(0));
    }

    @Test
    @DisplayName("getTopEntriesByCategory filters by category")
    void getTopEntriesByCategory() {
        when(leaderboardRepository.loadAll()).thenReturn(List.of(
                entry("Alice", 90, "java"),
                entry("Bob", 85, "math"),
                entry("Carol", 70, "java")));
        List<LeaderboardEntry> result = leaderboardService.getTopEntriesByCategory("java");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.category().equals("java")));
    }
}