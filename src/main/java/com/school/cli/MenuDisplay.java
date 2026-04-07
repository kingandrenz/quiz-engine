package com.school.cli;

import com.school.model.LeaderboardEntry;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.model.Answer;

import java.util.List;

public class MenuDisplay {

    private static final String DIVIDER = "─".repeat(50);

    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println("       QUIZ ENGINE — Test Your Knowledge");
        System.out.println(DIVIDER);
    }

    public void showMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("  1. Start Quiz");
        System.out.println("  2. View Leaderboard");
        System.out.println("  3. Exit");
        System.out.print("\nEnter choice: ");
    }

    public void showCategoryPrompt() {
        System.out.print("Enter category (or press ENTER for all categories): ");
    }

    public void showLimitPrompt() {
        System.out.print("How many questions? (1-20): ");
    }

    public void showNamePrompt() {
        System.out.print("Enter your name: ");
    }

    public void showQuestion(int number, int total, Question question) {
        System.out.println("\n" + DIVIDER);
        System.out.printf("Question %d of %d  [%s]%n", number, total, question.category());
        System.out.println(question.text());

        if (question.answer() instanceof Answer.MultipleChoice mc) {
            List<String> options = mc.options();
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  %d. %s%n", i, options.get(i));
            }
            System.out.print("Your answer (enter number): ");
        } else {
            System.out.print("Your answer (true/false): ");
        }
    }

    public void showAnswerFeedback(boolean correct) {
        System.out.println(correct ? "  ✓ Correct!" : "  ✗ Wrong.");
    }

    public void showResult(QuizResult result) {
        System.out.println("\n" + DIVIDER);
        System.out.println("Quiz Complete!");
        System.out.printf("Player   : %s%n", result.playerName());
        System.out.printf("Score    : %d / %d%n", result.score(), result.totalQuestions());
        System.out.printf("Result   : %.1f%%%n", result.percentage());
        System.out.printf("Category : %s%n", result.category());
        System.out.println(DIVIDER);
    }

    public void showLeaderboard(List<LeaderboardEntry> entries) {
        System.out.println("\n" + DIVIDER);
        System.out.println("  LEADERBOARD — Top Scores");
        System.out.println(DIVIDER);
        if (entries.isEmpty()) {
            System.out.println("  No scores yet. Be the first!");
        } else {
            System.out.printf("  %-4s %-20s %-10s %-8s%n", "Rank", "Player", "Score", "Category");
            System.out.println("  " + "─".repeat(46));
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                System.out.printf("  %-4d %-20s %-10.1f %-8s%n",
                        i + 1, e.playerName(), e.percentage(), e.category());
            }
        }
        System.out.println(DIVIDER);
    }

    public void showError(String message) {
        System.out.println("  [Error] " + message);
    }

    public void showMessage(String message) {
        System.out.println("  " + message);
    }
}