package de.bwvschule.itf233.gruppe3.quizgame.config;

import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

@Configuration
public class MariaDBConfig {

    private static final String DB_NAME = "quizdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DRIVER_CLASS = "org.mariadb.jdbc.Driver";

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
        String rootUrl = "jdbc:mariadb://localhost:" + port + "/";
        String dbUrl = "jdbc:mariadb://localhost:" + port + "/" + DB_NAME;

        // Create the database schema if it does not exist yet
        try (Connection conn = DriverManager.getConnection(rootUrl, DB_USER, DB_PASSWORD)) {
            conn.createStatement().execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        // Seed the database from quizdb.sql only when it is still empty
        try (Connection conn = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD)) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "question", null)) {
                if (!rs.next()) {
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                    populator.addScript(new ClassPathResource("quizdb.sql"));
                    populator.setSqlScriptEncoding("UTF-8");
                    populator.setIgnoreFailedDrops(true);
                    populator.setSeparator(ScriptUtils.DEFAULT_STATEMENT_SEPARATOR);
                    populator.populate(conn);
                }
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName(DRIVER_CLASS);
        return new HikariDataSource(config);
    }
}
