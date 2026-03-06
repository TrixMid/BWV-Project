package de.bwvschule.itf233.gruppe3.quizgame.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.DriverManager;

public class DBProfileSelector implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String mysqlUrl = environment.getProperty("mysql.datasource.url");
        String mysqlUsername = environment.getProperty("mysql.datasource.username");
        String mysqlPassword = environment.getProperty("mysql.datasource.password");
        String mysqlDriver = environment.getProperty("mysql.datasource.driver-class-name");

        try {
            Class.forName(mysqlDriver);
            DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword).close();
            environment.addActiveProfile("mysql");
            System.out.println("Activating mysql profile.");
        } catch (Exception e) {
            environment.addActiveProfile("sqlite");
            System.out.println("Activating sqlite profile.");
        }
    }
}
