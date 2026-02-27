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

    public List<Question> getQuestionsByQuestionSet(Integer questionSetId) {
        return questionRepository.findByQuestionSetId(questionSetId);
    }

    public List<McAnswer> getAnswersForQuestion(Integer questionId) {
        return mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(questionId);
    }

    public boolean checkSingleChoiceAnswer(Integer questionId, Integer answerId) {
        List<McAnswer> answers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(questionId);

        for (McAnswer answer : answers) {
            if (answer.getId().equals(answerId)) {
                return answer.isCorrect();
            }
        }

        return false;
    }
}