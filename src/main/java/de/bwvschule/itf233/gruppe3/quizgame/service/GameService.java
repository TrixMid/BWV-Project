package de.bwvschule.itf233.gruppe3.quizgame.service;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.*;
import de.bwvschule.itf233.gruppe3.quizgame.dto.GapAnswerDto;
import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.bwvschule.itf233.gruppe3.quizgame.dto.ReviewQuestionResponse;
import de.bwvschule.itf233.gruppe3.quizgame.dto.ReviewAnswerResponse;

import de.bwvschule.itf233.gruppe3.quizgame.dto.ReviewSummaryResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class GameService {

    //diese GameService enthält die zentrale Spiellogik.
    private final PlayerRepository playerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final RoomProgressRepository roomProgressRepository;
    private final AnsweredQuestionRepository answeredQuestionRepository;
    private final RoomRepository roomRepository;
    private final QuestionRepository questionRepository;
    private final McAnswerRepository mcAnswerRepository;
    private final GapFieldRepository gapFieldRepository;
    private final GapOptionRepository gapOptionRepository;

    @Transactional
    public GameSession startNewSession(Player player) {
        Room startRoom = roomRepository.findAllByOrderByRoomOrderAsc().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein Raum vorhanden"));
        GameSession session = GameSession.builder()
                .player(player)
                .currentRoom(startRoom)
                .status(GameStatus.ACTIVE)
                .totalPoints(0)
                .build();
        session = gameSessionRepository.save(session);
        // Fortschritt für Startraum anlegen
        createRoomProgress(session, startRoom);
        return session;
    }

    @Transactional
    public RoomProgress createRoomProgress(GameSession session, Room room) {
        return roomProgressRepository.save(
                RoomProgress.builder()
                        .gameSession(session)
                        .room(room)
                        .pointsEarned(0)
                        .completed(false)
                        .medal(MedalType.NONE)
                        .build()
        );
    }

    @Transactional
    public void changeRoom(GameSession session, Room targetRoom) {
        session.setCurrentRoom(targetRoom);
        // Prüfen, ob bereits ein RoomProgress existiert
        Optional<RoomProgress> existing = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(session.getSessionId(), targetRoom.getRoomId());
        if (existing.isEmpty()) {
            createRoomProgress(session, targetRoom);
        }
        gameSessionRepository.save(session);
    }

    @Transactional
    public GameSession startSessionForPlayerName(String playerName) {
        String cleanedName = playerName == null ? "" : playerName.trim();

        if (cleanedName.isBlank()) {
            throw new IllegalArgumentException("Playername darf nicht leer sein.");
        }

        Player player = playerRepository.findByUsername(cleanedName)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(cleanedName);
                    return playerRepository.save(newPlayer);
                });

        return gameSessionRepository
                .findFirstByPlayerPlayerIdAndStatusOrderBySessionIdDesc(player.getPlayerId(), GameStatus.ACTIVE)
                .orElseGet(() -> startNewSession(player));
    }
    // MC/TF Antwort auswerten
    @Transactional
    public AnsweredQuestion evaluateMcAnswer(GameSession session, RoomProgress progress,
                                             Question question, List<Integer> selectedAnswerIds) {
        List<McAnswer> allAnswers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(question.getId());

        int pointsEarned = (int) allAnswers.stream()
                .filter(answer -> selectedAnswerIds.contains(answer.getId()))
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .count();

        long correctAnswerCount = allAnswers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .count();

        boolean correct = pointsEarned == correctAnswerCount && correctAnswerCount > 0;

        String selectedAnswerIdsText = selectedAnswerIds.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        AnsweredQuestion aq = AnsweredQuestion.builder()
                .roomProgress(progress)
                .question(question)
                .pointsEarned(pointsEarned)
                .correct(correct)
                .selectedAnswerIds(selectedAnswerIdsText)
                .build();

        aq = answeredQuestionRepository.save(aq);

        progress.setPointsEarned(progress.getPointsEarned() + pointsEarned);
        session.setTotalPoints(session.getTotalPoints() + pointsEarned);

        updateRoomCompletion(progress);
        return aq;
    }

    @Transactional(readOnly = true)
    public ReviewSummaryResponse getReviewSummary(Integer sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();

        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();

        int totalPoints = progress.getPointsEarned();

        int maxPoints = answeredQuestionRepository
                .findByRoomProgressProgressIdOrderByAnsweredAtAsc(progress.getProgressId())
                .stream()
                .map(AnsweredQuestion::getQuestion)
                .mapToInt(question -> {
                    List<McAnswer> answers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(question.getId());
                    return (int) answers.stream()
                            .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                            .count();
                })
                .sum();

        int scoreOutOf100 = maxPoints > 0
                ? (int) Math.round((totalPoints * 100.0) / maxPoints)
                : 0;

        String medal;
        if (scoreOutOf100 >= 90) {
            medal = "GOLD";
        } else if (scoreOutOf100 >= 75) {
            medal = "SILVER";
        } else if (scoreOutOf100 >= 50) {
            medal = "BRONZE";
        } else {
            medal = "NONE";
        }

        return new ReviewSummaryResponse(
                session.getSessionId(),
                session.getPlayer().getUsername(),
                totalPoints,
                maxPoints,
                scoreOutOf100,
                medal
        );
    }

    // GAP Antwort auswerten (pro Lücke eine gewählte Option)
    @Transactional
    public void evaluateGapAnswer(GameSession session, RoomProgress progress,
                                  List<GapAnswerDto> gapAnswers) {
        int totalPoints = 0;
        for (GapAnswerDto dto : gapAnswers) {
            GapOption selected = gapOptionRepository.findById(dto.getSelectedOptionId())
                    .orElseThrow();
            totalPoints += selected.getPoints();
        }

        Question question = questionRepository.findById(gapAnswers.get(0).getQuestionId())
                .orElseThrow();
        AnsweredQuestion aq = AnsweredQuestion.builder()
                .roomProgress(progress)
                .question(question)
                .pointsEarned(totalPoints)
                .correct(totalPoints > 0)
                .build();
        answeredQuestionRepository.save(aq);

        progress.setPointsEarned(progress.getPointsEarned() + totalPoints);
        session.setTotalPoints(session.getTotalPoints() + totalPoints);
        updateRoomCompletion(progress);
    }

    private Set<Integer> parseSelectedAnswerIds(String selectedAnswerIds) {
        if (selectedAnswerIds == null || selectedAnswerIds.isBlank()) {
            return Set.of();
        }

        return java.util.Arrays.stream(selectedAnswerIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<ReviewQuestionResponse> getReviewForSession(Integer sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId).orElseThrow();

        RoomProgress progress = roomProgressRepository
                .findByGameSessionSessionIdAndRoomRoomId(sessionId, session.getCurrentRoom().getRoomId())
                .orElseThrow();

        List<AnsweredQuestion> answeredQuestions = answeredQuestionRepository.findByRoomProgressProgressIdOrderByAnsweredAtAsc(progress.getProgressId());

        return answeredQuestions.stream().map(aq -> {
            Question question = aq.getQuestion();

            Set<Integer> selectedIds = parseSelectedAnswerIds(aq.getSelectedAnswerIds());

            List<ReviewAnswerResponse> answers = mcAnswerRepository
                    .findByQuestionIdOrderByOptionOrderAsc(question.getId())
                    .stream()
                    .map(answer -> new ReviewAnswerResponse(
                            answer.getId(),
                            answer.getOptionText(),
                            answer.getOptionOrder(),
                            answer.getIsCorrect(),
                            selectedIds.contains(answer.getId())
                    ))
                    .toList();

            int maxPoints = question.getPoints() != null ? question.getPoints() : 0;

            return new ReviewQuestionResponse(
                    question.getId(),
                    question.getStartText(),
                    question.getImageUrl(),
                    question.getEndText(),
                    aq.getPointsEarned(),
                    maxPoints,
                    aq.getCorrect(),
                    answers
            );
        }).toList();
    }

    private void updateRoomCompletion(RoomProgress progress) {
        Room room = progress.getRoom();
        if (progress.getPointsEarned() >= room.getMinPointsRequired()) {
            progress.setCompleted(true);

            int maxPoints = room.getMaxPoints();
            double percentage = (double) progress.getPointsEarned() / maxPoints;
            if (percentage >= 0.9) {
                progress.setMedal(MedalType.GOLD);
            } else if (percentage >= 0.75) {
                progress.setMedal(MedalType.SILVER);
            } else if (percentage >= 0.5) {
                progress.setMedal(MedalType.BRONZE);
            }
        }
        roomProgressRepository.save(progress);
    }
}