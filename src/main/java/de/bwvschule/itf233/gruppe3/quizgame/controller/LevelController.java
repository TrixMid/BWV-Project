package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionSet;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionSetRepository;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities.Room;
import de.bwvschule.itf233.gruppe3.quizgame.gamelogic.repository.RoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final QuestionRepository questionRepository;
    private final RoomRepository roomRepository;
    private final QuestionSetRepository questionSetRepository;

    public LevelController(QuestionRepository questionRepository,
                           RoomRepository roomRepository,
                           QuestionSetRepository questionSetRepository) {
        this.questionRepository = questionRepository;
        this.roomRepository = roomRepository;
        this.questionSetRepository = questionSetRepository;
    }

    @GetMapping("/{level}")
    public List<Question> getLevel(@PathVariable int level) {
        Room room = roomRepository.findByRoomOrder(level)
                .orElseThrow(() -> new RuntimeException("Kein Raum mit order " + level + " gefunden"));

        // Team des Raums ermitteln (über question_set -> team)
        QuestionSet roomSet = room.getQuestionSet(); // Das Set, das dem Raum zugeordnet ist
        Integer teamId = roomSet.getTeam().getId();

        // Alle Question-Sets dieses Teams laden
        List<QuestionSet> allSets = questionSetRepository.findByTeamId(teamId);

        // Alle Fragen aus allen Sets sammeln
        List<Question> allQuestions = new ArrayList<>();
        for (QuestionSet set : allSets) {
            allQuestions.addAll(questionRepository.findByQuestionSetId(set.getId()));
        }

        return allQuestions;
    }
}