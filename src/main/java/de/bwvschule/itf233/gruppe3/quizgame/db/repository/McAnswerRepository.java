package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.McAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface McAnswerRepository extends JpaRepository<McAnswer, Integer> {
    List<McAnswer> findByQuestionQuestionIdOrderByOptionOrderAsc(Integer questionId);
}