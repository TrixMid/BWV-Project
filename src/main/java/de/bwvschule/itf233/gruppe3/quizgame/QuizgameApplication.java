package de.bwvschule.itf233.gruppe3.quizgame;

import de.bwvschule.itf233.gruppe3.quizgame.config.DBProfileSelector;
import de.bwvschule.itf233.gruppe3.quizgame.desktop.GameWindow;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.util.Arrays;

@SpringBootApplication
public class QuizgameApplication {

    static SpringApplicationBuilder setupApplicationBuilder() {
        return new SpringApplicationBuilder(QuizgameApplication.class)
                .initializers(new DBProfileSelector());
    }

    static ConfigurableApplicationContext runApplication(boolean headless, String[] args) {
        return setupApplicationBuilder().headless(headless).run(args);
    }

	public static void main(String[] args) {
        boolean isDesktop = Arrays.asList(args).contains("--desktop");
        if(isDesktop) {
            System.setProperty("java.awt.headless", "false");
            ConfigurableApplicationContext context = runApplication(false, args);
            SwingUtilities.invokeLater(GameWindow::launch);
        } else {
            runApplication(true, args);
        }
	}
}
