package de.bwvschule.itf233.gruppe3.quizgame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.DriverManager;

@Configuration
public class DBConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() {
        try {
            DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword).close();
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(mysqlUrl);
            dataSource.setUsername(mysqlUsername);
            dataSource.setPassword(mysqlPassword);
            System.out.println("Successfully connected to MySQL.");
            return dataSource;
        } catch (Exception e) {
            System.out.println("MySQL connection failed, falling back to SQLite.");
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.sqlite.JDBC");
            dataSource.setUrl("jdbc:sqlite:quizgame.db");
            dataSource.setUsername("");
            dataSource.setPassword("");
            return dataSource;
        }
    }
}