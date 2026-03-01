package de.bwvschule.itf233.gruppe3.quizgame.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerOptionRequest(
        @NotBlank String answerText,
        @NotNull Integer points
) {
}
