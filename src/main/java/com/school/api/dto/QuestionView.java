package com.school.api.dto;

import com.school.model.Answer;
import com.school.model.Question;

import java.util.List;

public record QuestionView(
        String id,
        String text,
        String category,
        String answerType,
        List<String> options // null for true/false questions
) {
    public static QuestionView from(Question question) {
        if (question.answer() instanceof Answer.MultipleChoice mc) {
            return new QuestionView(
                    question.id(),
                    question.text(),
                    question.category(),
                    "multiple_choice",
                    mc.options());
        } else {
            return new QuestionView(
                    question.id(),
                    question.text(),
                    question.category(),
                    "true_false",
                    null);
        }
    }
}