package de.bwvschule.itf233.gruppe3.quizgame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LevelWebController {

    @GetMapping("/levels")
    public String levels() {
        return "levels";
    }

    @GetMapping("/levels2")
    public String levels2() {
        return "levels2";
    }

    @GetMapping("/levelroom")
    public String levelroom() {
        return "levelroom";
    }

    @GetMapping("/reviewroom")
    public String reviewroom() {
        return "reviewroom";
    }

}