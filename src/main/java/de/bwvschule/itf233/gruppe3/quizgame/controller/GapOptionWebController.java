package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapField;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.GapOption;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.GapFieldRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.GapOptionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions/{questionId}/gapfields/{gapFieldId}/gapoptions")
public class GapOptionWebController {

    private final GapOptionRepository gapOptionRepository;
    private final GapFieldRepository gapFieldRepository;

    public GapOptionWebController(GapOptionRepository gapOptionRepository, GapFieldRepository gapFieldRepository) {
        this.gapOptionRepository = gapOptionRepository;
        this.gapFieldRepository = gapFieldRepository;
    }

    @GetMapping
    public String gapOptions(@PathVariable Integer questionId, @PathVariable Integer gapFieldId, Model model) {
        GapField gapField = gapFieldRepository.findById(gapFieldId).orElseThrow();
        model.addAttribute("gapField", gapField);
        model.addAttribute("questionId", questionId);
        model.addAttribute("gapOptions", gapOptionRepository.findByGapFieldIdOrderByOptionOrderAsc(gapFieldId));
        model.addAttribute("gapOption", new GapOption());
        return "gapoptions";
    }

    @PostMapping
    public String addGapOption(@PathVariable Integer questionId, @PathVariable Integer gapFieldId, @ModelAttribute GapOption gapOption) {
        GapField gapField = gapFieldRepository.findById(gapFieldId).orElseThrow();
        gapOption.setGapField(gapField);
        gapOptionRepository.save(gapOption);
        return "redirect:/questions/" + questionId + "/gapfields/" + gapFieldId + "/gapoptions";
    }
}
