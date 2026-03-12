package de.bwvschule.itf233.gruppe3.quizgame.service;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.*;
import de.bwvschule.itf233.gruppe3.quizgame.dto.GapAnswerDto;
import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    //diese GameService enthält die zentrale Spiellogik.
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

    // MC/TF Antwort auswerten
    @Transactional
    public AnsweredQuestion evaluateMcAnswer(GameSession session, RoomProgress progress,
                                             Question question, List<Integer> selectedAnswerIds) {
        List<McAnswer> allAnswers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(question.getId());
        int pointsEarned = allAnswers.stream()
                .filter(a -> selectedAnswerIds.contains(a.getId()))
                .mapToInt(McAnswer::getPoints)
                .sum();

        boolean correct = (pointsEarned > 0); // einfache Definition: positiv = richtig

        AnsweredQuestion aq = AnsweredQuestion.builder()
                .roomProgress(progress)
                .question(question)
                .pointsEarned(pointsEarned)
                .correct(correct)
                .build();
        aq = answeredQuestionRepository.save(aq);

        progress.setPointsEarned(progress.getPointsEarned() + pointsEarned);
        session.setTotalPoints(session.getTotalPoints() + pointsEarned);

        updateRoomCompletion(progress);
        return aq;
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