package de.bwvschule.itf233.gruppe3.quizgame.service;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.*;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.*;
import de.bwvschule.itf233.gruppe3.quizgame.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
            answers = mcAnswerRepository.findByQuestionQuestionIdOrderByOptionOrderAsc(questionId)
                    .stream()
                    .map(a -> new McAnswerResponse(
                            a.getAnswerId(),
                            a.getOptionText(),
                            a.getOptionOrder()
                    ))
                    .toList();
        }

        if (question.getQuestionType() == QuestionType.GAP) {
            gaps = gapFieldRepository.findByQuestionQuestionIdOrderByGapIndexAsc(questionId)
                    .stream()
                    .map(gap -> new GapFieldResponse(
                            gap.getGapId(),
                            gap.getGapIndex(),
                            gap.getTextBefore(),
                            gap.getTextAfter(),
                            gapOptionRepository.findByGapFieldGapIdOrderByOptionOrderAsc(gap.getGapId())
                                    .stream()
                                    .map(opt -> new GapOptionResponse(
                                            opt.getGapOptionId(),
                                            opt.getOptionText(),
                                            opt.getOptionOrder()
                                    ))
                                    .toList()
                    ))
                    .toList();
        }

        return new QuestionDetailResponse(
                question.getQuestionId(),
                question.getQuestionSet().getQuestionSetId(),
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

        List<McAnswer> allAnswers = mcAnswerRepository.findByQuestionQuestionIdOrderByOptionOrderAsc(question.getQuestionId());

        Set<Integer> correctAnswerIds = allAnswers.stream()
                .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                .map(McAnswer::getAnswerId)
                .collect(Collectors.toSet());

        Set<Integer> selectedIds = request.selectedAnswerIds();

        boolean correct = selectedIds.equals(correctAnswerIds);
        int earnedPoints = correct ? question.getPoints() : 0;

        return new SubmitAnswerResponse(
                question.getQuestionId(),
                correct,
                earnedPoints,
                correctAnswerIds
        );
    }
}