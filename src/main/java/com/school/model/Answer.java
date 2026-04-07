package com.school.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Answer.MultipleChoice.class, name = "multiple_choice"),
        @JsonSubTypes.Type(value = Answer.TrueFalse.class, name = "true_false")
})
public sealed interface Answer permits Answer.MultipleChoice, Answer.TrueFalse {

    boolean isCorrect(String userInput);

    record MultipleChoice(
            java.util.List<String> options,
            int correctIndex) implements Answer {
        @Override
        public boolean isCorrect(String userInput) {
            try {
                return Integer.parseInt(userInput.trim()) == correctIndex;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    record TrueFalse(boolean correctAnswer) implements Answer {
        @Override
        public boolean isCorrect(String userInput) {
            String normalised = userInput.trim().toLowerCase();
            boolean given = normalised.equals("true") || normalised.equals("t") || normalised.equals("1");
            return given == correctAnswer;
        }
    }
}