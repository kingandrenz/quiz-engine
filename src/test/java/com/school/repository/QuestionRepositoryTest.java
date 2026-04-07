package com.school.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.school.exception.QuizException;
import com.school.model.Answer;
import com.school.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRepositoryTest {

    @TempDir
    Path tempDir;

    private Path questionsFile;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        questionsFile = tempDir.resolve("questions.json");
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private void writeQuestions(List<Question> questions) throws Exception {
        mapper.writeValue(questionsFile.toFile(), questions);
    }

    @Test
    @DisplayName("loadAll returns all questions from JSON file")
    void loadAllReturnsAllQuestions() throws Exception {
        var q1 = new Question("q1", "Is Java OOP?", "java", new Answer.TrueFalse(true));
        var q2 = new Question("q2", "Best language?", "general",
                new Answer.MultipleChoice(List.of("Java", "Python"), 0));
        writeQuestions(List.of(q1, q2));

        var repo = new QuestionRepository(questionsFile);
        List<Question> result = repo.loadAll();

        assertEquals(2, result.size());
        assertEquals("q1", result.get(0).id());
    }

    @Test
    @DisplayName("loadByCategory filters correctly")
    void loadByCategoryFilters() throws Exception {
        var q1 = new Question("q1", "Java question?", "java", new Answer.TrueFalse(true));
        var q2 = new Question("q2", "Math question?", "math", new Answer.TrueFalse(false));
        var q3 = new Question("q3", "Java again?", "java", new Answer.TrueFalse(true));
        writeQuestions(List.of(q1, q2, q3));

        var repo = new QuestionRepository(questionsFile);
        List<Question> result = repo.loadByCategory("java");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(q -> q.category().equals("java")));
    }

    @Test
    @DisplayName("loadByCategory is case insensitive")
    void loadByCategoryIsCaseInsensitive() throws Exception {
        var q1 = new Question("q1", "Java question?", "java", new Answer.TrueFalse(true));
        writeQuestions(List.of(q1));

        var repo = new QuestionRepository(questionsFile);
        assertEquals(1, repo.loadByCategory("JAVA").size());
    }

    @Test
    @DisplayName("missing file throws QuizException")
    void missingFileThrowsQuizException() {
        var repo = new QuestionRepository(tempDir.resolve("missing.json"));
        assertThrows(QuizException.class, repo::loadAll);
    }
}