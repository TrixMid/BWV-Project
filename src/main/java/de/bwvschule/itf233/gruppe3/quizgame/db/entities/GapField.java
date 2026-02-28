package de.bwvschule.itf233.gruppe3.quizgame.db.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gap_field")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GapField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_id")
    private Integer gapId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "gap_index", nullable = false)
    private Integer gapIndex;

    @Column(name = "text_before", columnDefinition = "TEXT")
    private String textBefore;

    @Column(name = "text_after", columnDefinition = "TEXT")
    private String textAfter;
}