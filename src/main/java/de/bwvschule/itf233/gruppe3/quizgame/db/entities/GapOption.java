package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "gap_option")
public class GapOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_option_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gap_id", nullable = false)
    private GapField gapField;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    //zusatzliches Feld
    @Column(name = "points", nullable = false, columnDefinition = "integer default 0")
    private Integer points = 0;

    public GapOption() {
    }

    public GapOption(Integer id, GapField gapField, String optionText, Boolean isCorrect, Integer optionOrder) {
        this.id = id;
        this.gapField = gapField;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.optionOrder = optionOrder;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GapField getGapField() {
        return gapField;
    }

    public void setGapField(GapField gapField) {
        this.gapField = gapField;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}