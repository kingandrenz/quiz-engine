package com.school.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.api.dto.SubmitAnswersRequest;
import com.school.exception.QuizException;
import com.school.model.Answer;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.service.LeaderboardService;
import com.school.service.QuizService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    QuizService quizService;
    @MockBean
    LeaderboardService leaderboardService;

    private final Question q1 = new Question("q1", "Is Java OOP?", "java",
            new Answer.TrueFalse(true));
    private final Question q2 = new Question("q2", "Best language?", "java",
            new Answer.MultipleChoice(List.of("Java", "Python"), 0));

    @Test
    @DisplayName("GET /api/quiz/start returns questions without correct answers")
    void startQuizReturnsQuestionViews() throws Exception {
        when(quizService.prepareQuiz(any(), anyInt())).thenReturn(List.of(q1, q2));

        mockMvc.perform(get("/api/quiz/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("q1"))
                .andExpect(jsonPath("$.data[0].answerType").value("true_false"))
                // correct answer must NOT be present
                .andExpect(jsonPath("$.data[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.data[0].correctIndex").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/quiz/start with unknown category returns 400")
    void startQuizUnknownCategoryReturns400() throws Exception {
        when(quizService.prepareQuiz(eq("unknown"), anyInt()))
                .thenThrow(new QuizException("No questions found for category: unknown"));

        mockMvc.perform(get("/api/quiz/start").param("category", "unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/quiz/categories returns distinct sorted categories")
    void getCategoriesReturnsSortedList() throws Exception {
        var mathQ = new Question("q3", "2+2?", "math",
                new Answer.MultipleChoice(List.of("3", "4"), 1));
        when(quizService.prepareQuiz(isNull(), eq(100)))
                .thenReturn(List.of(q1, q2, mathQ));

        mockMvc.perform(get("/api/quiz/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value("java"))
                .andExpect(jsonPath("$.data[1]").value("math"));
    }

    @Test
    @DisplayName("POST /api/quiz/submit returns score summary")
    void submitQuizReturnsScore() throws Exception {
        when(quizService.prepareQuiz(eq("java"), eq(100))).thenReturn(List.of(q1));
        when(quizService.evaluate(anyString(), anyList(), anyList()))
                .thenReturn(new QuizResult("Alice", 1, 1, "java", Instant.now()));

        SubmitAnswersRequest request = new SubmitAnswersRequest(
                "Alice", "java", List.of("q1"), List.of("true"));

        mockMvc.perform(post("/api/quiz/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.playerName").value("Alice"))
                .andExpect(jsonPath("$.data.score").value(1))
                .andExpect(jsonPath("$.data.percentage").value(100.0));
    }

    @Test
    @DisplayName("POST /api/quiz/submit with no matching IDs returns 400")
    void submitQuizNoMatchingIdsReturns400() throws Exception {
        when(quizService.prepareQuiz(eq("java"), eq(100))).thenReturn(List.of(q1));

        SubmitAnswersRequest request = new SubmitAnswersRequest(
                "Alice", "java", List.of("nonexistent"), List.of("true"));

        mockMvc.perform(post("/api/quiz/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}