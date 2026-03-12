package de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {
    List<GameSession> findByPlayerPlayerIdOrderByLastSavedAtDesc(Integer playerId);
}
