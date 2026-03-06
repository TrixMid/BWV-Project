package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionType;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionSetRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.ThemeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/questions")
public class QuestionWebController {

    private final QuestionRepository questionRepository;
    private final QuestionSetRepository questionSetRepository;
    private final ThemeRepository themeRepository;

    public QuestionWebController(QuestionRepository questionRepository, QuestionSetRepository questionSetRepository, ThemeRepository themeRepository) {
        this.questionRepository = questionRepository;
        this.questionSetRepository = questionSetRepository;
        this.themeRepository = themeRepository;
    }

    @GetMapping
    public String questions(Model model) {
        model.addAttribute("questions", questionRepository.findAll());
        model.addAttribute("question", new Question());
        model.addAttribute("questionSets", questionSetRepository.findAll());
        model.addAttribute("themes", themeRepository.findAll());
        model.addAttribute("questionTypes", Arrays.asList(QuestionType.values()));
        return "questions";
    }

    @PostMapping
    public String addQuestion(Question question) {
        questionRepository.save(question);
        return "redirect:/questions";
    }
}
