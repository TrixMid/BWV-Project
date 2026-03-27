package de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByOrderByRoomOrderAsc();

    Optional<Room> findByRoomOrder(Integer roomOrder);
}
