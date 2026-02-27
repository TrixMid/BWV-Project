package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "gap_field",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_gap_field_question_index", columnNames = {"question_id", "gap_index"})
        },
        indexes = {
                @Index(name = "idx_gap_field_question", columnList = "question_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class GapField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "gap_index", nullable = false)
    private int gapIndex;

    @Column(name = "input_type", nullable = false, length = 10)
    //private String inputType = "FREE_TEXT";
    private GapInputType inputType = GapInputType.FREE_TEXT;

    @Lob
    @Column(name = "correct_text")
    private String correctText;

    @Column(name = "case_sensitive", nullable = false)
    private boolean caseSensitive = false;

    @OneToMany(mappedBy = "gapField", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("optionOrder ASC")
    private List<GapOption> options = new ArrayList<>();

   /* // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) {
        this.question = question; }

    public int getGapIndex() { return gapIndex; }
    public void setGapIndex(int gapIndex) { this.gapIndex = gapIndex; }

    public String getCorrectText() { return correctText; }
    public void setCorrectText(String correctText) { this.correctText = correctText; }

    public boolean isCaseSensitive() { return caseSensitive; }
    public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }

    public List<GapOption> getOptions() { return options; }
    public void setOptions(List<GapOption> options) { this.options = options; }*/
}