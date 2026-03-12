package de.bwvschule.itf233.gruppe3.quizgame.gamedb.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_connection")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Integer connectionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_room_id", nullable = false)
    private Room fromRoom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_room_id", nullable = false)
    private Room toRoom;

    @Column(name = "label")
    private String label;

    @Column(name = "requires_completion", nullable = false)
    private Boolean requiresCompletion = true;
}
