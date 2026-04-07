package com.school.config;

import com.school.repository.LeaderboardRepository;
import com.school.repository.QuestionRepository;
import com.school.service.LeaderboardService;
import com.school.service.QuizService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class AppConfig {

    @Value("${quiz.questions.path}")
    private String questionsPath;

    @Value("${quiz.leaderboard.path}")
    private String leaderboardPath;

    @Bean
    public QuestionRepository questionRepository() {
        Path path = Path.of(questionsPath);
        return new QuestionRepository(
                Files.exists(path) ? path : null);
    }

    @Bean
    public LeaderboardRepository leaderboardRepository() {
        return new LeaderboardRepository(Path.of(leaderboardPath));
    }

    @Bean
    public QuizService quizService(QuestionRepository questionRepository) {
        return new QuizService(questionRepository);
    }

    @Bean
    public LeaderboardService leaderboardService(LeaderboardRepository leaderboardRepository) {
        return new LeaderboardService(leaderboardRepository);
    }
}