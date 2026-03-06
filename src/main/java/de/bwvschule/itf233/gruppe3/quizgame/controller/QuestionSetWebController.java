package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionSet;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionSetRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.TeamRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questionsets")
public class QuestionSetWebController {

    private final QuestionSetRepository questionSetRepository;
    private final TeamRepository teamRepository;

    public QuestionSetWebController(QuestionSetRepository questionSetRepository, TeamRepository teamRepository) {
        this.questionSetRepository = questionSetRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public String questionSets(Model model) {
        model.addAttribute("questionSets", questionSetRepository.findAll());
        model.addAttribute("questionSet", new QuestionSet());
        model.addAttribute("teams", teamRepository.findAll());
        return "questionsets";
    }

    @PostMapping
    public String addQuestionSet(QuestionSet questionSet) {
        questionSetRepository.save(questionSet);
        return "redirect:/questionsets";
    }
}
