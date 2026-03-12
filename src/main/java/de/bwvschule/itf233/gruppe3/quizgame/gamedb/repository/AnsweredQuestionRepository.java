package de.bwvschule.itf233.gruppe3.quizgame.gamedb.repository;

import de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities.AnsweredQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;

public interface AnsweredQuestionRepository extends JpaRepository<AnsweredQuestion, Integer> {
    boolean existsByRoomProgressProgressIdAndQuestionId(Integer progressId,
                                                                Integer questionId);

    @Query("SELECT aq.question.id FROM AnsweredQuestion aq WHERE aq.roomProgress.progressId = :progressId")
    Set<Integer> findAnsweredQuestionIdsByProgressId(Integer progressId);
}