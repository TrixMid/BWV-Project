package de.bwvschule.itf233.gruppe3.quizgame.service;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.*;
import de.bwvschule.itf233.gruppe3.quizgame.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final McAnswerRepository mcAnswerRepository;
    private final GapFieldRepository gapFieldRepository;
    private final GapOptionRepository gapOptionRepository;

    public QuestionService(QuestionRepository questionRepository, McAnswerRepository mcAnswerRepository, GapFieldRepository gapFieldRepository, GapOptionRepository gapOptionRepository) {
        this.questionRepository = questionRepository;
        this.mcAnswerRepository = mcAnswerRepository;
        this.gapFieldRepository = gapFieldRepository;
        this.gapOptionRepository = gapOptionRepository;
    }

/*    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }*/

    public QuestionDetailResponse getQuestionDetail(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        List<String> themeNames = question.getThemes()
                .stream()
                .map(Theme::getName)
                .toList();

        List<McAnswerResponse> answers = List.of();
        List<GapFieldResponse> gaps = List.of();

        if (question.getQuestionType() == QuestionType.MC || question.getQuestionType() == QuestionType.TF) {
            answers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(questionId)
                    .stream()
                    .map(a -> new McAnswerResponse(
                            a.getId(),
                            a.getOptionText(),
                            a.getOptionOrder()
                    ))
                    .toList();
        }

        if (question.getQuestionType() == QuestionType.GAP) {
            gaps = gapFieldRepository.findByQuestionIdOrderByGapIndexAsc(questionId)
                    .stream()
                    .map(gap -> new GapFieldResponse(
                            gap.getId(),
                            gap.getGapIndex(),
                            gap.getTextBefore(),
                            gap.getTextAfter(),
                            gapOptionRepository.findByGapFieldIdOrderByOptionOrderAsc(gap.getId())
                                    .stream()
                                    .map(opt -> new GapOptionResponse(
                                            opt.getId(),
                                            opt.getOptionText(),
                                            opt.getOptionOrder()
                                    ))
                                    .toList()
                    ))
                    .toList();
        }

        return new QuestionDetailResponse(
                question.getId(),
                question.getQuestionSet().getId(),
                question.getQuestionType(),
                question.getStartText(),
                question.getImageUrl(),
                question.getEndText(),
                question.getAllowsMultiple(),
                question.getPoints(),
                themeNames,
                answers,
                gaps
        );
    }

    public SubmitAnswerResponse submitMcOrTfAnswer(SubmitAnswerRequest request) {
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + request.questionId()));

        if (question.getQuestionType() == QuestionType.GAP) {
            throw new IllegalArgumentException("Use a separate submit API for GAP questions.");
        }

        List<McAnswer> allAnswers = mcAnswerRepository.findByQuestionIdOrderByOptionOrderAsc(question.getId());

        Set<Integer> correctAnswerIds = allAnswers.stream()
                .filter(McAnswer::getIsCorrect)
                .map(McAnswer::getId)
                .collect(Collectors.toSet());

        Set<Integer> selectedIds = request.selectedAnswerIds();

        boolean correct = selectedIds.equals(correctAnswerIds);
        int earnedPoints = correct ? question.getPoints() : 0;

        return new SubmitAnswerResponse(
                question.getId(),
                correct,
                earnedPoints,
                correctAnswerIds
        );
    }
}