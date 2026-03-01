package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.dto.*;
import de.bwvschule.itf233.gruppe3.quizgame.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{id}")
    public QuestionDetailResponse getQuestionById(@PathVariable Integer id) {
        return questionService.getQuestionDetail(id);
    }

    @PostMapping("/submit")
    public SubmitAnswerResponse submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        return questionService.submitMcOrTfAnswer(request);
    }
}