package de.bwvschule.itf233.gruppe3.quizgame.dto;

import java.util.List;

public record ReviewQuestionResponse(
        Integer questionId,
        String startText,
        String imageUrl,
        String endText,
        Integer pointsEarned,
        Integer maxPoints,
        Boolean correct,
        List<ReviewAnswerResponse> answers
) {
}