package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuestionType(QuestionType type);
    List<Question> findByQuestionSetId(Integer questionSetId);
    List<Question> findByThemeId(Integer themesId);
}
