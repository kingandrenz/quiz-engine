package com.school.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.school.exception.QuizException;
import com.school.model.LeaderboardEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeaderboardRepository {

    private final ObjectMapper mapper;
    private final Path filePath;

    public LeaderboardRepository(Path filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public List<LeaderboardEntry> loadAll() {
        if (!Files.exists(filePath))
            return new ArrayList<>();
        try {
            LeaderboardEntry[] entries = mapper.readValue(filePath.toFile(), LeaderboardEntry[].class);
            return new ArrayList<>(Arrays.asList(entries));
        } catch (IOException e) {
            throw new QuizException("Failed to load leaderboard from: " + filePath, e);
        }
    }

    public void save(LeaderboardEntry entry) {
        List<LeaderboardEntry> entries = loadAll();
        entries.add(entry);
        Collections.sort(entries);
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), entries);
        } catch (IOException e) {
            throw new QuizException("Failed to save leaderboard to: " + filePath, e);
        }
    }

    public List<LeaderboardEntry> getTopN(int n) {
        return loadAll().stream().limit(n).toList();
    }
}