package de.bwvschule.itf233.gruppe3.quizgame.gamelogic.entities;

import de.bwvschule.itf233.gruppe3.quizgame.db.entities.QuestionSet;
import de.bwvschule.itf233.gruppe3.quizgame.db.entities.Theme;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(name = "min_points_required", nullable = false)
    private Integer minPointsRequired = 10;

    @Column(name = "max_points", nullable = false)
    private Integer maxPoints = 30;

    @Column(name = "map_x")
    private Integer mapX;

    @Column(name = "map_y")
    private Integer mapY;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "room_order", nullable = false)
    private Integer roomOrder = 0;

    @ManyToOne
    @JoinColumn(name = "question_set_id")
    private QuestionSet questionSet;

    @OneToMany(mappedBy = "fromRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomConnection> connections = new ArrayList<>();
}
