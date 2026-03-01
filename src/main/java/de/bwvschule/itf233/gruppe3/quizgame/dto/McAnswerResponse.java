package de.bwvschule.itf233.gruppe3.quizgame.dto;

public record McAnswerResponse(
        Integer answerId,
        String optionText,
        Integer optionOrder
) {
}