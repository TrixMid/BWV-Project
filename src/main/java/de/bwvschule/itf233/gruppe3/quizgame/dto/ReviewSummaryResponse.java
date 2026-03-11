package de.bwvschule.itf233.gruppe3.quizgame.dto;

public record ReviewSummaryResponse(
        Integer sessionId,
        String playerName,
        Integer totalPoints,
        Integer maxPoints,
        Integer scoreOutOf100,
        String medal
) {
}