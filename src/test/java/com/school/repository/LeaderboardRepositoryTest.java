package com.school.repository;

import com.school.model.LeaderboardEntry;
import com.school.model.QuizResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardRepositoryTest {

    @TempDir
    Path tempDir;

    private Path leaderboardFile;
    private LeaderboardRepository repo;

    @BeforeEach
    void setUp() {
        leaderboardFile = tempDir.resolve("leaderboard.json");
        repo = new LeaderboardRepository(leaderboardFile);
    }

    private LeaderboardEntry entryFor(String name, double pct) {
        return LeaderboardEntry.from(
                new QuizResult(name, (int) pct, 100, "java", Instant.now()));
    }

    @Test
    @DisplayName("loadAll returns empty list when file does not exist")
    void emptyWhenNoFile() {
        assertTrue(repo.loadAll().isEmpty());
    }

    @Test
    @DisplayName("save persists an entry and it can be reloaded")
    void savePersistsEntry() {
        repo.save(entryFor("Alice", 80));
        List<LeaderboardEntry> entries = repo.loadAll();
        assertEquals(1, entries.size());
        assertEquals("Alice", entries.get(0).playerName());
    }

    @Test
    @DisplayName("entries are sorted highest percentage first")
    void sortedByPercentageDescending() {
        repo.save(entryFor("Alice", 60));
        repo.save(entryFor("Bob", 90));
        repo.save(entryFor("Carol", 75));

        List<LeaderboardEntry> entries = repo.loadAll();
        assertEquals("Bob", entries.get(0).playerName());
        assertEquals("Carol", entries.get(1).playerName());
        assertEquals("Alice", entries.get(2).playerName());
    }

    @Test
    @DisplayName("getTopN returns only N entries")
    void getTopNReturnsCorrectCount() {
        repo.save(entryFor("Alice", 60));
        repo.save(entryFor("Bob", 90));
        repo.save(entryFor("Carol", 75));

        assertEquals(2, repo.getTopN(2).size());
        assertEquals("Bob", repo.getTopN(2).get(0).playerName());
    }
}