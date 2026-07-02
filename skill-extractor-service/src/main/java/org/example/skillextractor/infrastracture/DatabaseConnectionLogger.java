package org.example.skillextractor.infrastracture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConnectionLogger {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionLogger.class);
    private final DataSource dataSource;

    public DatabaseConnectionLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logDbConnection() {
        logger.info("Rozpoczynanie połączenia z bazą danych AWS RDS...");
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                logger.info("Wynik połączenia: Pomyślnie nawiązano połączenie z bazą danych AWS RDS.");
            } else {
                logger.warn("Wynik połączenia: Nawiązano połączenie z bazą danych AWS RDS, ale jest ono nieprawidłowe.");
            }
        } catch (SQLException e) {
            logger.error("Wynik połączenia: Błąd podczas próby połączenia z bazą danych AWS RDS. Szczegóły: {}", e.getMessage());
        }
    }
}
