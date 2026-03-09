package de.bwvschule.itf233.gruppe3.quizgame;

import de.bwvschule.itf233.gruppe3.quizgame.config.DBProfileSelector;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class QuizgameApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(QuizgameApplication.class)
				.initializers(new DBProfileSelector())
				.run(args);
	}
}
