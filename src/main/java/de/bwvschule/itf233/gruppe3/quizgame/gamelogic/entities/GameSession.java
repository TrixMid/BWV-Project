package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//gespeicherte Spielstand des Players
@Entity
@Table(name = "game_session")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private GameStatus status = GameStatus.ACTIVE;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_saved_at")
    private LocalDateTime lastSavedAt;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomProgress> roomProgressList = new ArrayList<>();
}
