package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Theme;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeRepository themeRepository;

    @PostMapping
    public Theme create(@RequestBody Theme theme) {
        return themeRepository.save(theme);
    }

    @GetMapping
    public List<Theme> getAll() {
        return themeRepository.findAll();
    }
}