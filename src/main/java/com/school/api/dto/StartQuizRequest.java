package com.school.api.dto;

public record StartQuizRequest(
        String category,
        int limit) {
    public StartQuizRequest {
        if (limit <= 0)
            limit = 10;
        if (limit > 20)
            limit = 20;
    }
}