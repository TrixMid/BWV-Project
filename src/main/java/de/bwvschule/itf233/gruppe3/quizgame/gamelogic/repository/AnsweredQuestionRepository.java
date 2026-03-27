package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.AnsweredQuestion;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.RoomProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface AnsweredQuestionRepository extends JpaRepository<AnsweredQuestion, Integer> {

    boolean existsByRoomProgressProgressIdAndQuestionId(Integer progressId, Integer questionId);

    @Query("SELECT aq.question.id FROM AnsweredQuestion aq WHERE aq.roomProgress.progressId = :progressId")
    Set<Integer> findAnsweredQuestionIdsByProgressId(Integer progressId);

    long countByRoomProgressGameSessionSessionId(Integer sessionId);

    // Neue Methode: zählt die beantworteten Fragen für einen bestimmten RoomProgress
    long countByRoomProgress(RoomProgress roomProgress);

    List<AnsweredQuestion> findByRoomProgressProgressIdOrderByAnsweredAtAsc(Integer progressId);
}