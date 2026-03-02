import dao.ProblemDaoImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ProblemService;
import ui.ConsoleUI;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Properties properties = new Properties();

        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream(
                "database.properties"
        )) {
            properties.load(stream);
            logger.info("Successfully loaded database configuration.");
        } catch (Exception e) {
            logger.error("The database configuration file could not be read.", e);
            throw new RuntimeException("The application launch was interrupted due to an error reading the database configuration file.");
        }

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            logger.info("Database connection opened.");

            var service = new ProblemService(new ProblemDaoImplementation(connection));
            var ui = new ConsoleUI(service, new Scanner(System.in));

            ui.start();
        } catch (Exception e) {
            logger.error("A critical error occurred while the application was running.", e);
            throw new RuntimeException("The app was stopped in an emergency.", e);
        }
    }
}