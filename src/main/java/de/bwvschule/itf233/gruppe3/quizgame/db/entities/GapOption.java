package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "gap_option",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_gap_option_gap_order", columnNames = {"gap_id", "option_order"})
        },
        indexes = {
                @Index(name = "idx_gap_option_gap", columnList = "gap_id")
        }
)

@Getter
@Setter
public class GapOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_option_id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "gap_id", nullable = false)
    private GapField gapField;

    @Lob
    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;

    @Column(name = "option_order", nullable = false)
    private int optionOrder;

/*    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public GapField getGapField() { return gapField; }
    public void setGapField(GapField gapField) {
        this.gapField = gapField; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getOptionOrder() { return optionOrder; }
    public void setOptionOrder(int optionOrder) { this.optionOrder = optionOrder; }*/
}