package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {
    Optional<Theme> findByName(String name);
}