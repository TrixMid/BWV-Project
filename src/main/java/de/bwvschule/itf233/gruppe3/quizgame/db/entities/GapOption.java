package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gap_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GapOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gap_option_id")
    private Integer gapOptionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gap_id", nullable = false)
    private GapField gapField;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;
}