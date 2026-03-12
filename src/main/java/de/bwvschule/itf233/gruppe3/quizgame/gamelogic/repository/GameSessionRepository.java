package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.GameSession;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {
    List<GameSession> findByPlayerPlayerIdOrderByLastSavedAtDesc(Integer playerId);

    Optional<GameSession> findFirstByPlayerPlayerIdAndStatusOrderBySessionIdDesc(Integer playerId, GameStatus status);
}