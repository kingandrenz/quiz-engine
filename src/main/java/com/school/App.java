package com.school;

import com.school.cli.MenuDisplay;
import com.school.cli.QuizRunner;
import com.school.repository.LeaderboardRepository;
import com.school.repository.QuestionRepository;
import com.school.service.LeaderboardService;
import com.school.service.QuizService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--cli")) {
            runCli();
        } else {
            SpringApplication.run(App.class, args);
        }
    }

    private static void runCli() {
        Path questionsPath = Path.of("src/main/resources/questions.json");
        Path leaderboardPath = Path.of("leaderboard.json");

        QuestionRepository questionRepo = new QuestionRepository(questionsPath);
        LeaderboardRepository leaderboardRepo = new LeaderboardRepository(leaderboardPath);
        QuizService quizService = new QuizService(questionRepo);
        LeaderboardService leaderboardService = new LeaderboardService(leaderboardRepo);

        new QuizRunner(quizService, leaderboardService, new MenuDisplay(), new Scanner(System.in)).run();
    }
}