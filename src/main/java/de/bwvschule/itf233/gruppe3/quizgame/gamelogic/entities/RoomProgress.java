package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "room_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer progressId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "points_earned", nullable = false)
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "medal", nullable = false)
    @Builder.Default
    private MedalType medal = MedalType.NONE;

    @OneToMany(mappedBy = "roomProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AnsweredQuestion> answeredQuestions = new ArrayList<>();
}
