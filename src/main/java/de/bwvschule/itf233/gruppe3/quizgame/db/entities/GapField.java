package de.bwvschule.itf233.gruppe3.quizgame.db.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "gap_field")
public class GapField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "gap_index", nullable = false)
    private Integer gapIndex;

    @Column(name = "text_before", columnDefinition = "TEXT")
    private String textBefore;

    @Column(name = "text_after", columnDefinition = "TEXT")
    private String textAfter;

    public GapField() {
    }

    public GapField(Integer id, Question question, Integer gapIndex, String textBefore, String textAfter) {
        this.id = id;
        this.question = question;
        this.gapIndex = gapIndex;
        this.textBefore = textBefore;
        this.textAfter = textAfter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getGapIndex() {
        return gapIndex;
    }

    public void setGapIndex(Integer gapIndex) {
        this.gapIndex = gapIndex;
    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }
}
