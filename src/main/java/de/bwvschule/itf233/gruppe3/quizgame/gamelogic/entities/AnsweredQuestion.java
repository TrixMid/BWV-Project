package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answered_question",
        uniqueConstraints = @UniqueConstraint(columnNames = {"progress_id", "question_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnsweredQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answered_id")
    private Integer answeredId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "progress_id", nullable = false)
    private RoomProgress roomProgress;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    @Column(name = "correct", nullable = false)
    private Boolean correct;

    @Column(name = "selected_answer_ids", columnDefinition = "TEXT")
    private String selectedAnswerIds;

    @Column(name = "answered_at", nullable = false)
    @Builder.Default
    private LocalDateTime answeredAt = LocalDateTime.now();
}
