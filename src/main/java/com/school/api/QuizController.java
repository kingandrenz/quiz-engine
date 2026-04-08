package com.school.api;

import com.school.api.dto.ApiResponse;
import com.school.api.dto.QuestionView;
import com.school.api.dto.SubmitAnswersRequest;
import com.school.exception.QuizException;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.service.LeaderboardService;
import com.school.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final LeaderboardService leaderboardService;

    public QuizController(QuizService quizService, LeaderboardService leaderboardService) {
        this.quizService = quizService;
        this.leaderboardService = leaderboardService;
    }

    // GET /api/quiz/start?category=java&limit=5
    @GetMapping("/start")
    public ResponseEntity<ApiResponse<List<QuestionView>>> startQuiz(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<QuestionView> questions = quizService.prepareQuiz(category, limit)
                    .stream()
                    .map(QuestionView::from)
                    .toList();
            return ResponseEntity.ok(ApiResponse.ok("Quiz ready", questions));
        } catch (QuizException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/quiz/submit
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitQuiz(
            @RequestBody SubmitAnswersRequest request) {
        try {
            List<Question> questions = quizService.prepareQuiz(request.category(), 100)
                    .stream()
                    .filter(q -> request.questionIds().contains(q.id()))
                    .toList();

            if (questions.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No matching questions found for submitted IDs"));
            }

            QuizResult result = quizService.evaluate(
                    request.playerName(), questions, request.answers());

            leaderboardService.recordResult(result);

            Map<String, Object> response = Map.of(
                    "playerName", result.playerName(),
                    "score", result.score(),
                    "totalQuestions", result.totalQuestions(),
                    "percentage", result.percentage(),
                    "category", result.category());

            return ResponseEntity.ok(ApiResponse.ok("Quiz submitted successfully", response));
        } catch (QuizException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/quiz/categories
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = quizService.prepareQuiz(null, 100)
                .stream()
                .map(Question::category)
                .distinct()
                .sorted()
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }

    // GET /
    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, String>>> welcome() {
        Map<String, String> endpoints = Map.of(
                "start quiz", "/api/quiz/start?category=java&limit=5",
                "all categories", "/api/quiz/categories",
                "submit answers", "/api/quiz/submit (POST)",
                "leaderboard", "/api/leaderboard",
                "leaderboard by category", "/api/leaderboard/category?name=java");
        return ResponseEntity.ok(ApiResponse.ok("Quiz Engine API is running", endpoints));
    }
}