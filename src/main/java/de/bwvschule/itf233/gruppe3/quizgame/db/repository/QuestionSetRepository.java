package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface QuestionSetRepository extends JpaRepository<QuestionSet, Integer> {
    List<QuestionSet> findByTeamId(Integer teamId);
    List<QuestionSet> findByTeamName(String teamName);
}