package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "start_text", columnDefinition = "TEXT")
    private String startText;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "end_text", columnDefinition = "TEXT")
    private String endText;

    @Column(name = "allows_multiple", nullable = false)
    private Boolean allowsMultiple = false;

    @Column(nullable = false)
    private Integer points = 1;

    @ManyToMany
    @JoinTable(
            name = "Question_Theme",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> themes = new HashSet<>();

    public Question() {
    }

    public Question(Integer id, QuestionSet questionSet, QuestionType questionType, String startText, String imageUrl, String endText, Boolean allowsMultiple, Integer points, Set<Theme> themes) {
        this.id = id;
        this.questionSet = questionSet;
        this.questionType = questionType;
        this.startText = startText;
        this.imageUrl = imageUrl;
        this.endText = endText;
        this.allowsMultiple = allowsMultiple;
        this.points = points;
        this.themes = themes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEndText() {
        return endText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public Boolean getAllowsMultiple() {
        return allowsMultiple;
    }

    public void setAllowsMultiple(Boolean allowsMultiple) {
        this.allowsMultiple = allowsMultiple;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Set<Theme> getThemes() {
        return themes;
    }

    public void setThemes(Set<Theme> themes) {
        this.themes = themes;
    }
}