package de.bwvschule.itf233.gruppe3.quizgame.service;


import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.McAnswerRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final McAnswerRepository mcAnswerRepository;

    public QuestionService(QuestionRepository questionRepository, McAnswerRepository mcAnswerRepository) {
        this.questionRepository = questionRepository;
        this.mcAnswerRepository = mcAnswerRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Integer id) {
        return questionRepository.findById(id);
    }
}