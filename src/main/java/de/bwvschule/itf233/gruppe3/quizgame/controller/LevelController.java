package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final QuestionRepository questionRepository;

    public LevelController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping("/{level}")
    public List<Question> getLevel(@PathVariable int level) {

        int questionsPerLevel = 16;

        int start = (level - 1) * questionsPerLevel + 1;
        int end = start + questionsPerLevel - 1;

        return questionRepository.findByIdBetween(start, end);
    }
}