package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionSetRepository extends JpaRepository<QuestionSet, Integer> {
    List<QuestionSet> findByTeamTeamId(Integer teamId);
}