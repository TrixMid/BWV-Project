package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question")
@Setter
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer id;

//    @Column(name = "question_set_id", nullable = false)
//    private Integer questionSetId;

    @ManyToOne
    @JoinColumn(name = "question_set_id")
    private QuestionSet questionSet;

    @ManyToOne
    private Theme theme;

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
    private boolean allowsMultiple;

    @Column(nullable = false)
    private int points;

/*    public Question() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionSetId() {
        return questionSet.getId();
    }

    public void setQuestionSetId(Integer questionSetId) {
        this.questionSet = questionSetId;
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

    public boolean isAllowsMultiple() {
        return allowsMultiple;
    }

    public void setAllowsMultiple(boolean allowsMultiple) {
        this.allowsMultiple = allowsMultiple;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }*/
}