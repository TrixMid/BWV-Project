package de.bwvschule.itf233.gruppe3.quizgame;

import de.bwvschule.itf233.gruppe3.quizgame.db.repository.QuestionRepository;
import de.bwvschule.itf233.gruppe3.quizgame.db.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("sqlite")
class DatabaseIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void testDataSeeding() {
        // We seeded 49 questions in data.sql
        long count = questionRepository.count();
        assertThat(count).isEqualTo(49);

        // Verify team name
        teamRepository.findById(1).ifPresent(team -> {
            assertThat(team.getName()).isEqualTo("BWV Wirtschaft und Sozialkunde");
        });
    }
}
