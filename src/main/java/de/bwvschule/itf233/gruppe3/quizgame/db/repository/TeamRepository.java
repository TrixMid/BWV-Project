package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}