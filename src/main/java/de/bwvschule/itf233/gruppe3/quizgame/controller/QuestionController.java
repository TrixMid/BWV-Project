package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.service.QuestionService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Integer id) {
        Optional<Question> question = questionService.getQuestionById(id);
        return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/answers")
    public List<McAnswer> getAnswersForQuestion(@PathVariable Integer id) {
        return questionService.getAnswersForQuestion(id);
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<Map<String, Object>> checkAnswer(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer answerId = body.get("answerId");

        if (answerId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "answerId fehlt"
            ));
        }

        boolean correct = questionService.checkSingleChoiceAnswer(id, answerId);

        return ResponseEntity.ok(Map.of(
                "questionId", id,
                "answerId", answerId,
                "correct", correct
        ));
    }
}