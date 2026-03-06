package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapField;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.GapFieldRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions/{questionId}/gapfields")
public class GapFieldWebController {

    private final GapFieldRepository gapFieldRepository;
    private final QuestionRepository questionRepository;

    public GapFieldWebController(GapFieldRepository gapFieldRepository, QuestionRepository questionRepository) {
        this.gapFieldRepository = gapFieldRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public String gapFields(@PathVariable Integer questionId, Model model) {
        Question question = questionRepository.findById(questionId).orElseThrow();
        model.addAttribute("question", question);
        model.addAttribute("gapFields", gapFieldRepository.findByQuestionIdOrderByGapIndexAsc(questionId));
        model.addAttribute("gapField", new GapField());
        return "gapfields";
    }

    @PostMapping
    public String addGapField(@PathVariable Integer questionId, @ModelAttribute GapField gapField) {
        Question question = questionRepository.findById(questionId).orElseThrow();
        gapField.setQuestion(question);
        gapFieldRepository.save(gapField);
        return "redirect:/questions/" + questionId + "/gapfields";
    }
}
