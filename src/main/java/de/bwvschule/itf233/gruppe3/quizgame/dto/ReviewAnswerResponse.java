package de.bwvschule.itf233.gruppe3.quizgame.dto;

public record ReviewAnswerResponse(
        Integer answerId,
        String optionText,
        Integer optionOrder,
        Boolean correct,
        Boolean selected
) {
}
