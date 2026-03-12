package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // Fragen eines QuestionSets
    List<Question> findByQuestionSet_Id(Integer questionSetId);

    // Fragenbereich für Level
    List<Question> findByIdBetween(Integer start, Integer end);

}