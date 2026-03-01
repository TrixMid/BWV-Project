package de.bwvschule.itf233.gruppe3.quizgame.dto;

import java.util.Set;

public record SubmitAnswerResponse(
        Integer questionId,
        boolean correct,
        Integer earnedPoints,
        Set<Integer> correctAnswerIds
) {
}