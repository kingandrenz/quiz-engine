package com.school.service;

import com.school.exception.QuizException;
import com.school.model.Answer;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    QuestionRepository questionRepository;

    QuizService quizService;

    private final Question q1 = new Question("q1", "Is Java OOP?", "java", new Answer.TrueFalse(true));
    private final Question q2 = new Question("q2", "Java has pointers?", "java", new Answer.TrueFalse(false));
    private final Question q3 = new Question("q3", "Best language?", "java",
            new Answer.MultipleChoice(List.of("Java", "Python"), 0));

    @BeforeEach
    void setUp() {
        quizService = new QuizService(questionRepository);
    }

    @Nested
    @DisplayName("prepareQuiz")
    class PrepareQuiz {

        @Test
        @DisplayName("returns shuffled subset up to limit")
        void returnsSubsetUpToLimit() {
            when(questionRepository.loadByCategory("java")).thenReturn(List.of(q1, q2, q3));
            List<Question> result = quizService.prepareQuiz("java", 2);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("returns all questions when limit exceeds available")
        void limitExceedsAvailable() {
            when(questionRepository.loadByCategory("java")).thenReturn(List.of(q1, q2));
            List<Question> result = quizService.prepareQuiz("java", 10);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("throws QuizException when no questions found")
        void throwsWhenEmpty() {
            when(questionRepository.loadByCategory("math")).thenReturn(List.of());
            assertThrows(QuizException.class, () -> quizService.prepareQuiz("math", 5));
        }

        @Test
        @DisplayName("null category loads all questions")
        void nullCategoryLoadsAll() {
            when(questionRepository.loadAll()).thenReturn(List.of(q1, q2, q3));
            List<Question> result = quizService.prepareQuiz(null, 5);
            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("evaluate")
    class Evaluate {

        @Test
        @DisplayName("all correct answers returns full score")
        void allCorrect() {
            List<Question> questions = List.of(q1, q2);
            List<String> answers = List.of("true", "false");
            QuizResult result = quizService.evaluate("Alice", questions, answers);
            assertEquals(2, result.score());
            assertEquals(2, result.totalQuestions());
            assertEquals(100.0, result.percentage(), 0.001);
        }

        @Test
        @DisplayName("no correct answers returns zero score")
        void noneCorrect() {
            List<Question> questions = List.of(q1, q2);
            List<String> answers = List.of("false", "true");
            QuizResult result = quizService.evaluate("Bob", questions, answers);
            assertEquals(0, result.score());
        }

        @Test
        @DisplayName("mixed answers returns partial score")
        void mixedAnswers() {
            List<Question> questions = List.of(q1, q2, q3);
            List<String> answers = List.of("true", "true", "0");
            QuizResult result = quizService.evaluate("Carol", questions, answers);
            assertEquals(2, result.score());
        }

        @Test
        @DisplayName("mismatched answer count throws QuizException")
        void mismatchThrows() {
            List<Question> questions = List.of(q1, q2);
            List<String> answers = List.of("true");
            assertThrows(QuizException.class,
                    () -> quizService.evaluate("Dave", questions, answers));
        }

        @Test
        @DisplayName("single category quiz sets category correctly")
        void singleCategorySetCorrectly() {
            List<Question> questions = List.of(q1, q2);
            List<String> answers = List.of("true", "false");
            QuizResult result = quizService.evaluate("Alice", questions, answers);
            assertEquals("java", result.category());
        }

        @Test
        @DisplayName("mixed category quiz sets category to mixed")
        void mixedCategorySetToMixed() {
            var mathQ = new Question("q4", "2+2?", "math",
                    new Answer.MultipleChoice(List.of("3", "4"), 1));
            List<Question> questions = List.of(q1, mathQ);
            List<String> answers = List.of("true", "1");
            QuizResult result = quizService.evaluate("Alice", questions, answers);
            assertEquals("mixed", result.category());
        }
    }
}