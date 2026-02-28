package de.bwvschule.itf233.gruppe3.quizgame.dto;

import java.util.List;

public record GapFieldResponse(
        Integer gapId,
        Integer gapIndex,
        String textBefore,
        String textAfter,
        List<GapOptionResponse> options
) {
}