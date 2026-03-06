package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Theme;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.ThemeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/themes")
public class ThemeWebController {

    private final ThemeRepository themeRepository;

    public ThemeWebController(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @GetMapping
    public String themes(Model model) {
        model.addAttribute("themes", themeRepository.findAll());
        model.addAttribute("theme", new Theme());
        return "themes";
    }

    @PostMapping
    public String addTheme(Theme theme) {
        themeRepository.save(theme);
        return "redirect:/themes";
    }
}
