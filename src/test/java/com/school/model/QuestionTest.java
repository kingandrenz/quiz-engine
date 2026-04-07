package com.school.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Nested
    @DisplayName("Question construction")
    class Construction {

        @Test
        @DisplayName("valid question is created successfully")
        void validQuestion() {
            var answer = new Answer.TrueFalse(true);
            var q = new Question("q1", "Is Java compiled?", "general", answer);
            assertEquals("q1", q.id());
            assertEquals("general", q.category());
        }

        @Test
        @DisplayName("blank id throws IllegalArgumentException")
        void blankIdThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Question("", "Some text", "cat", new Answer.TrueFalse(false)));
        }

        @Test
        @DisplayName("null answer throws IllegalArgumentException")
        void nullAnswerThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Question("q2", "Some text", "cat", null));
        }
    }

    @Nested
    @DisplayName("MultipleChoice answer")
    class MultipleChoiceTests {

        private final Answer.MultipleChoice answer = new Answer.MultipleChoice(List.of("Java", "Python", "C++"), 0);

        @Test
        @DisplayName("correct index returns true")
        void correctIndex() {
            assertTrue(answer.isCorrect("0"));
        }

        @Test
        @DisplayName("wrong index returns false")
        void wrongIndex() {
            assertFalse(answer.isCorrect("2"));
        }

        @Test
        @DisplayName("non-numeric input returns false")
        void nonNumeric() {
            assertFalse(answer.isCorrect("Java"));
        }
    }

    @Nested
    @DisplayName("TrueFalse answer")
    class TrueFalseTests {

        private final Answer.TrueFalse trueAnswer = new Answer.TrueFalse(true);
        private final Answer.TrueFalse falseAnswer = new Answer.TrueFalse(false);

        @Test
        @DisplayName("'true' input matches true answer")
        void trueInput() {
            assertTrue(trueAnswer.isCorrect("true"));
        }

        @Test
        @DisplayName("'t' shorthand accepted")
        void tShorthand() {
            assertTrue(trueAnswer.isCorrect("T"));
        }

        @Test
        @DisplayName("'false' input matches false answer")
        void falseInput() {
            assertTrue(falseAnswer.isCorrect("false"));
        }

        @Test
        @DisplayName("wrong boolean returns false")
        void wrongBoolean() {
            assertFalse(trueAnswer.isCorrect("false"));
        }
    }

    @Nested
    @DisplayName("QuizResult percentage")
    class PercentageTests {

        @Test
        @DisplayName("3/10 correct = 30.0%")
        void threeOfTen() {
            var result = new QuizResult("Alice", 3, 10, "general",
                    java.time.Instant.now());
            assertEquals(30.0, result.percentage(), 0.001);
        }

        @Test
        @DisplayName("zero total questions returns 0.0")
        void zeroTotal() {
            var result = new QuizResult("Bob", 0, 0, "general",
                    java.time.Instant.now());
            assertEquals(0.0, result.percentage(), 0.001);
        }
    }
}