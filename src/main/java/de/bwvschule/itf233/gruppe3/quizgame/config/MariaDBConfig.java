package de.bwvschule.itf233.gruppe3.quizgame.config;

import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

@Configuration
public class MariaDBConfig {

    @Bean(name = "mariaDB4j")
    public MariaDB4jSpringService mariaDB4jSpringService() {
        return new MariaDB4jSpringService();
    }

    @Bean
    @Primary
    @DependsOn("mariaDB4j")
    public DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService) throws Exception {
        // Ensure MariaDB4j is running before attempting any connection
        if (!mariaDB4jSpringService.isRunning()) {
            mariaDB4jSpringService.start();
        }

        int port = mariaDB4jSpringService.getConfiguration().getPort();

        // Create the database schema if it does not exist yet
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mariadb://localhost:" + port + "/", "root", "")) {
            conn.createStatement().execute("CREATE DATABASE IF NOT EXISTS quizdb");
        }

        String url = "jdbc:mariadb://localhost:" + port + "/quizdb";

        // Seed the database from quizdb.sql only when it is still empty
        DriverManagerDataSource tempDs = new DriverManagerDataSource(url, "root", "");
        tempDs.setDriverClassName("org.mariadb.jdbc.Driver");

        try (Connection conn = tempDs.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "question", null)) {
                if (!rs.next()) {
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                    populator.addScript(new ClassPathResource("quizdb.sql"));
                    populator.setSqlScriptEncoding("UTF-8");
                    populator.setIgnoreFailedDrops(true);
                    populator.execute(tempDs);
                }
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("root");
        config.setPassword("");
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        return new HikariDataSource(config);
    }
}
