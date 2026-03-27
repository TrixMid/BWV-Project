package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<Player> findByUsername(String username);
    boolean existsByUsername(String username);
}
