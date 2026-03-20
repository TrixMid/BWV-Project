package de.bwvschule.itf233.gruppe3.quizgame.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SubmitAnswerRequest(
        @NotNull Integer questionId,
        @NotEmpty Set<Integer> selectedAnswerIds
) {
}