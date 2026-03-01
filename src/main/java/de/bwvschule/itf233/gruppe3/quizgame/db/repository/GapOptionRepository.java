package de.bwvschule.itf233.gruppe3.quizgame.db.repository;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GapOptionRepository extends JpaRepository<GapOption, Integer> {
    List<GapOption> findByGapFieldGapIdOrderByOptionOrderAsc(Integer gapId);
}