package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GapFieldRepository extends JpaRepository<GapField, Integer> {
    List<GapField> findByQuestionQuestionIdOrderByGapIndexAsc(Integer questionId);
}