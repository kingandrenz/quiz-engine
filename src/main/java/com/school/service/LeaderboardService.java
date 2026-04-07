package com.school.service;

import com.school.model.LeaderboardEntry;
import com.school.model.QuizResult;
import com.school.repository.LeaderboardRepository;

import java.util.List;

public class LeaderboardService {

    private static final int DEFAULT_TOP_N = 10;

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    public void recordResult(QuizResult result) {
        LeaderboardEntry entry = LeaderboardEntry.from(result);
        leaderboardRepository.save(entry);
    }

    public List<LeaderboardEntry> getTopEntries() {
        return leaderboardRepository.getTopN(DEFAULT_TOP_N);
    }

    public List<LeaderboardEntry> getTopEntries(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be greater than 0");
        return leaderboardRepository.getTopN(n);
    }

    public List<LeaderboardEntry> getTopEntriesByCategory(String category) {
        return leaderboardRepository.loadAll().stream()
                .filter(e -> e.category().equalsIgnoreCase(category))
                .limit(DEFAULT_TOP_N)
                .toList();
    }
}