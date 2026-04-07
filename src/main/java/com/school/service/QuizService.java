package com.school.service;

import com.school.exception.QuizException;
import com.school.model.Question;
import com.school.model.QuizResult;
import com.school.repository.QuestionRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizService {

    private final QuestionRepository questionRepository;

    public QuizService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> prepareQuiz(String category, int limit) {
        List<Question> questions = category == null || category.isBlank()
                ? questionRepository.loadAll()
                : questionRepository.loadByCategory(category);

        if (questions.isEmpty()) {
            throw new QuizException("No questions found for category: " + category);
        }

        List<Question> mutable = new ArrayList<>(questions);
        Collections.shuffle(mutable);
        return Collections.unmodifiableList(
                mutable.subList(0, Math.min(limit, mutable.size())));
    }

    public QuizResult evaluate(String playerName, List<Question> questions, List<String> userAnswers) {
        if (questions.size() != userAnswers.size()) {
            throw new QuizException("Answer count does not match question count");
        }

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).answer().isCorrect(userAnswers.get(i))) {
                score++;
            }
        }

        String category = questions.stream()
                .map(Question::category)
                .distinct()
                .count() == 1 ? questions.get(0).category() : "mixed";

        return new QuizResult(playerName, score, questions.size(), category, Instant.now());
    }
}