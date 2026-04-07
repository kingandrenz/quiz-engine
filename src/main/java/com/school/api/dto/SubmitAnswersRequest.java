package com.school.api.dto;

import java.util.List;

public record SubmitAnswersRequest(
        String playerName,
        String category,
        List<String> questionIds,
        List<String> answers) {
}