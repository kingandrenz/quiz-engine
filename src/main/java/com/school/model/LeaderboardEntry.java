package com.school.model;

import java.time.Instant;

public record LeaderboardEntry(
        String playerName,
        double percentage,
        int score,
        int totalQuestions,
        String category,
        Instant completedAt) implements Comparable<LeaderboardEntry> {

    public static LeaderboardEntry from(QuizResult result) {
        return new LeaderboardEntry(
                result.playerName(),
                result.percentage(),
                result.score(),
                result.totalQuestions(),
                result.category(),
                result.completedAt());
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Highest percentage first; ties broken by most recent
        int cmp = Double.compare(other.percentage(), this.percentage());
        return cmp != 0 ? cmp : other.completedAt().compareTo(this.completedAt());
    }
}