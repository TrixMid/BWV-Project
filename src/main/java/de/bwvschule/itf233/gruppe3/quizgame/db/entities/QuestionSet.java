package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "question_set",
        indexes = {
                @Index(name = "idx_question_set_team_id", columnList = "team_id")
        }
)
@Setter
public class QuestionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_set_id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    // getters/setters
    public Integer getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}