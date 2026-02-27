package de.bwvschule.itf233.gruppe3.quizgame.db.repository;


import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GapOptionRepository extends JpaRepository<GapOption, Integer> {
    List<GapOption> findByGapFieldIdOrderByOptionOrderAsc(Integer gapFieldId);
    List<GapOption> findByGapFieldIdAndCorrectTrue(Integer gapFieldId);
    void deleteByGapFieldId(Integer gapFieldId);
}