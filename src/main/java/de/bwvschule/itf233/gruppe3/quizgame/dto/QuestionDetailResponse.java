package de.bwvschule.itf233.gruppe3.quizgame.dto;


import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;

import java.util.List;

public record QuestionDetailResponse(
        Integer questionId,
        Integer questionSetId,
        QuestionType questionType,
        String startText,
        String imageUrl,
        String endText,
        Boolean allowsMultiple,
        Integer points,
        List<String> themes,
        List<McAnswerResponse> answers,
        List<GapFieldResponse> gaps
) {
}