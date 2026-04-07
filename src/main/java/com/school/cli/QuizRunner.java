package com.school.cli;

import com.school.exception.QuizException;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.service.LeaderboardService;
import com.school.service.QuizService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuizRunner {

    private final QuizService quizService;
    private final LeaderboardService leaderboardService;
    private final MenuDisplay display;
    private final Scanner scanner;

    public QuizRunner(QuizService quizService,
            LeaderboardService leaderboardService,
            MenuDisplay display,
            Scanner scanner) {
        this.quizService = quizService;
        this.leaderboardService = leaderboardService;
        this.display = display;
        this.scanner = scanner;
    }

    public void run() {
        display.showWelcome();
        boolean running = true;

        while (running) {
            display.showMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> startQuiz();
                case "2" -> display.showLeaderboard(leaderboardService.getTopEntries());
                case "3" -> {
                    display.showMessage("Goodbye!");
                    running = false;
                }
                default -> display.showError("Invalid choice. Please enter 1, 2 or 3.");
            }
        }
    }

    private void startQuiz() {
        display.showNamePrompt();
        String name = scanner.nextLine().trim();
        if (name.isBlank())
            name = "Anonymous";

        display.showCategoryPrompt();
        String category = scanner.nextLine().trim();

        display.showLimitPrompt();
        int limit = parseLimit(scanner.nextLine().trim());

        List<Question> questions;
        try {
            questions = quizService.prepareQuiz(
                    category.isBlank() ? null : category, limit);
        } catch (QuizException e) {
            display.showError(e.getMessage());
            return;
        }

        List<String> answers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            display.showQuestion(i + 1, questions.size(), questions.get(i));
            String answer = scanner.nextLine().trim();
            answers.add(answer);

            boolean correct = questions.get(i).answer().isCorrect(answer);
            display.showAnswerFeedback(correct);
        }

        QuizResult result = quizService.evaluate(name, questions, answers);
        display.showResult(result);
        leaderboardService.recordResult(result);
        display.showMessage("Your score has been saved to the leaderboard.");
    }

    private int parseLimit(String input) {
        try {
            int n = Integer.parseInt(input);
            return (n >= 1 && n <= 20) ? n : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }
}