package de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Integer playerId;

    @Column(nullable = false, unique = true)
    private String username;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
