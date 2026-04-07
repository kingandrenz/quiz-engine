package com.school.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Question(
        @JsonProperty("id") String id,
        @JsonProperty("text") String text,
        @JsonProperty("category") String category,
        @JsonProperty("answer") Answer answer) {
    // Compact constructor for validation
    public Question {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Question id must not be blank");
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Question text must not be blank");
        if (answer == null)
            throw new IllegalArgumentException("Question must have an answer");
    }
}