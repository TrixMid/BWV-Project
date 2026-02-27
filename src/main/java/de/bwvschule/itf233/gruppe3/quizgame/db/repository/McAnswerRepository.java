package de.bwvschule.itf233.gruppe3.quizgame.db.repository;


import de.bwvschule.itf233.gruppe3.quizgame.db.entities.McAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface McAnswerRepository extends JpaRepository<McAnswer, Integer> {
    List<McAnswer> findByQuestionId(Integer questionId);
    List<McAnswer> findByQuestionIdAndCorrectTrue(Integer questionId);
    List<McAnswer> findByQuestionIdOrderByOptionOrderAsc(Integer questionId);
}