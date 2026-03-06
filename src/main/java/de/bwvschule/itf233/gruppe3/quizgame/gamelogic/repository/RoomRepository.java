package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByOrderByRoomOrderAsc();
}
