package de.bwvschule.itf233.gruppe3.quizgame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/quiz")
    public String quiz() {
        return "quiz";
    }

}