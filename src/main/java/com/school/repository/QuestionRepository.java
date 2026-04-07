package com.school.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.school.exception.QuizException;
import com.school.model.Question;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuestionRepository {

    private final ObjectMapper mapper;
    private final Path filePath;

    public QuestionRepository(Path filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public List<Question> loadAll() {
        try {
            Question[] questions = mapper.readValue(filePath.toFile(), Question[].class);
            return Collections.unmodifiableList(Arrays.asList(questions));
        } catch (IOException e) {
            throw new QuizException("Failed to load questions from: " + filePath, e);
        }
    }

    public List<Question> loadByCategory(String category) {
        return loadAll().stream()
                .filter(q -> q.category().equalsIgnoreCase(category))
                .toList();
    }
}