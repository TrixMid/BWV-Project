package de.bwvschule.itf233.gruppe3.quizgame.controller;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Team;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.TeamRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teams")
public class TeamWebController {

    private final TeamRepository teamRepository;

    public TeamWebController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public String teams(Model model) {
        model.addAttribute("teams", teamRepository.findAll());
        model.addAttribute("team", new Team());
        return "teams";
    }

    @PostMapping
    public String addTeam(Team team) {
        teamRepository.save(team);
        return "redirect:/teams";
    }
}
