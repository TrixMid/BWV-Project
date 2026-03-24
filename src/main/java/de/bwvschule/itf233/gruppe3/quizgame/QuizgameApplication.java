package de.bwvschule.itf233.gruppe3.quizgame;

import de.bwvschule.itf233.gruppe3.quizgame.config.DBProfileSelector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@SpringBootApplication
public class QuizgameApplication {
    @Bean
    @Profile("sqlite")
    CommandLineRunner forceSeed(DataSource dataSource) {
        return args -> {
            ResourceDatabasePopulator resourceDatabasePopulator =
                    new ResourceDatabasePopulator(new ClassPathResource("data.sql"));
            resourceDatabasePopulator.execute(dataSource);
            System.out.println("LOG: Manually forced data.sql execution.");
        };
    }

	public static void main(String[] args) {
		new SpringApplicationBuilder(QuizgameApplication.class)
				.initializers(new DBProfileSelector())
				.run(args);
	}
}
