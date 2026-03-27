package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.dto.GapAnswerDto;
import de.bwvschule.itf233.gruppe3.quizgame.dto.ReviewQuestionResponse;
import de.bwvschule.itf233.gruppe3.quizgame.dto.SubmitAnswerRequest;
import de.bwvschule.itf233.gruppe3.quizgame.dto.SubmitAnswerResponse;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.AnsweredQuestion;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.GameSession;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.Room;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.RoomProgress;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.AnsweredQuestionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.GameSessionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.RoomProgressRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.RoomRepository;
import de.bwvschule.itf233.gruppe3.quizgame.service.GameService;
import de.bwvschule.itf233.gruppe3.quizgame.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import de.bwvschule.itf233.gruppe3.quizgame.dto.ReviewSummaryResponse;

import java.util.*;

@RestController
@RequestMapping("/quizgame")
@RequiredArgsConstructor
public class GameController {

    private final AnsweredQuestionRepository answeredQuestionRepository;
    private final GameService gameService;
    private final GameSessionRepository gameSessionRepository;
    private final RoomProgressRepository roomProgressRepository;
    private final RoomRepository roomRepository;
    private final QuestionService questionService;

    @PostMapping("/session/{sessionId}/answer/mc")
    public SubmitAnswerResponse submitMcAnswer(@PathVariable Integer sessionId,
                                               @RequestBody SubmitAnswerRequest request) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();

        AnsweredQuestion aq = gameService.evaluateMcAnswer(session, progress,
                questionService.getQuestionById(request.questionId()),
                new ArrayList<>(request.selectedAnswerIds()));

        return new SubmitAnswerResponse(aq.getQuestion().getId(), aq.getCorrect(),
                aq.getPointsEarned(), null);
    }

    @GetMapping("/session/{sessionId}/progress")
    public Map<String, Object> getSessionProgress(@PathVariable Integer sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session nicht gefunden: " + sessionId));

        // RoomProgress für den aktuellen Raum holen
        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow(() -> new IllegalStateException("Kein RoomProgress für diesen Raum gefunden"));

        long answeredCount = answeredQuestionRepository.countByRoomProgress(progress);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("currentQuestionIndex", (int) answeredCount);

        return response;
    }

    @GetMapping("/session/{sessionId}/review")
    public List<ReviewQuestionResponse> getSessionReview(@PathVariable Integer sessionId) {
        return gameService.getReviewForSession(sessionId);
    }

    @PostMapping("/session/start")
    public Map<String, Object> startSession(@RequestBody Map<String, Object> body) {
        String playerName = body.get("playerName") != null
                ? body.get("playerName").toString().trim()
                : "";

        GameSession session = gameService.startSessionForPlayerName(playerName);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getSessionId());
        response.put("playerName", session.getPlayer().getUsername());

        return response;
    }

    @PostMapping("/session/{sessionId}/answer/gap")
    public void submitGapAnswer(@PathVariable Integer sessionId,
                                @RequestBody List<GapAnswerDto> gapAnswers) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();
        gameService.evaluateGapAnswer(session, progress, gapAnswers);
    }

    @PostMapping("/session/{sessionId}/change-room/{roomId}")
    public void changeRoom(@PathVariable Integer sessionId, @PathVariable Integer roomId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        Room target = roomRepository.findById(roomId).orElseThrow();
        gameService.changeRoom(session, target);
    }

    @GetMapping("/session/{sessionId}/summary")
    public ReviewSummaryResponse getSessionSummary(@PathVariable Integer sessionId) {
        return gameService.getReviewSummary(sessionId);
    }
}