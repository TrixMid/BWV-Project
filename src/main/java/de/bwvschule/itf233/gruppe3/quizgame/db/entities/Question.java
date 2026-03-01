package de.bwvschule.itf233.gruppe3.quizgame.db.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @Column(name = "start_text", columnDefinition = "TEXT")
    private String startText;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "end_text", columnDefinition = "TEXT")
    private String endText;

    @Column(name = "allows_multiple", nullable = false)
    private Boolean allowsMultiple = false;

    @Column(nullable = false)
    private Integer points = 1;

    @ManyToMany
    @JoinTable(
            name = "Question_Theme",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<Theme> themes = new HashSet<>();
}