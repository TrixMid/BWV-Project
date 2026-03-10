package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.dto.GapAnswerDto;
import de.bwvschule.itf233.gruppe3.quizgame.dto.SubmitAnswerRequest;
import de.bwvschule.itf233.gruppe3.quizgame.dto.SubmitAnswerResponse;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.GameSessionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.RoomProgressRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.RoomRepository;
import de.bwvschule.itf233.gruppe3.quizgame.service.GameService;
import de.bwvschule.itf233.gruppe3.quizgame.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/quizgame")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameSessionRepository gameSessionRepository;
    private final RoomProgressRepository roomProgressRepository;
    private final RoomRepository roomRepository;
    private final QuestionService questionService;

    @PostMapping("/session/{sessionId}/answer/mc")
    public SubmitAnswerResponse submitMcAnswer(@PathVariable Integer sessionId,
                                               @RequestBody SubmitAnswerRequest request) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();

        AnsweredQuestion aq = gameService.evaluateMcAnswer(session, progress,
                questionService.getQuestionById(request.questionId()),
                new ArrayList<>(request.selectedAnswerIds()));

        return new SubmitAnswerResponse(aq.getQuestion().getId(), aq.getCorrect(),
                aq.getPointsEarned(), null);
    }

    @PostMapping("/session/{sessionId}/answer/gap")
    public void submitGapAnswer(@PathVariable Integer sessionId,
                                @RequestBody List<GapAnswerDto> gapAnswers) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();
        gameService.evaluateGapAnswer(session, progress, gapAnswers);
    }

    @PostMapping("/session/{sessionId}/change-room/{roomId}")
    public void changeRoom(@PathVariable Integer sessionId, @PathVariable Integer roomId) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();
        Room target = roomRepository.findById(roomId).orElseThrow();
        gameService.changeRoom(session, target);
    }
}