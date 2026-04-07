package com.school.model;

import java.time.Instant;

public record QuizResult(
        String playerName,
        int score,
        int totalQuestions,
        String category,
        Instant completedAt) {
    public double percentage() {
        if (totalQuestions == 0)
            return 0.0;
        return (score * 100.0) / totalQuestions;
    }
}