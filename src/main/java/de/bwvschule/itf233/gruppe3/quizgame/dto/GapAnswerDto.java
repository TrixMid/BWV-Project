package de.bwvschule.itf233.gruppe3.quizgame.dto;

import lombok.Data;

@Data
public class GapAnswerDto {
    private Integer questionId;
    private Integer gapFieldId;
    private Integer selectedOptionId;
}