package de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.RoomProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomProgressRepository extends JpaRepository<RoomProgress, Integer> {
    Optional<RoomProgress> findByGameSessionSessionIdAndRoomRoomId(Integer sessionId, Integer roomId);
    List<RoomProgress> findByGameSessionSessionId(Integer sessionId);
}
