package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.McAnswer;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.McAnswerRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions/{questionId}/mcanswers")
public class McAnswerWebController {

    private final McAnswerRepository mcAnswerRepository;
    private final QuestionRepository questionRepository;

    public McAnswerWebController(McAnswerRepository mcAnswerRepository, QuestionRepository questionRepository) {
        this.mcAnswerRepository = mcAnswerRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public String mcAnswers(@PathVariable Integer questionId, Model model) {
        Question question = questionRepository.findById(questionId).orElseThrow();
        model.addAttribute("question", question);
        model.addAttribute("mcAnswers", mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(questionId));
        model.addAttribute("mcAnswer", new McAnswer());
        return "mcanswers";
    }

    @PostMapping
    public String addMcAnswer(@PathVariable Integer questionId, @ModelAttribute McAnswer mcAnswer) {
        Question question = questionRepository.findById(questionId).orElseThrow();
        mcAnswer.setQuestion(question);
        mcAnswerRepository.save(mcAnswer);
        return "redirect:/questions/" + questionId + "/mcanswers";
    }
}
