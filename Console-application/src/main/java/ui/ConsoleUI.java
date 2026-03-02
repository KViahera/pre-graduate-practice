package ui;

import exception.DataAccessException;
import exception.EntityNotFoundException;
import model.Problem;
import service.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private final ProblemService service;
    private final Scanner scanner;

    public ConsoleUI(ProblemService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> retrieveAll();
                case "2" -> create();
                case "3" -> retrieveById();
                case "4" -> update();
                case "5" -> delete();
                case "0" -> { return; }
                default -> System.out.println("Command '" + choice + "' not recognized. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nMain Menu: ");
        System.out.println("1. View all problems in the archive");
        System.out.println("2. Add a new problem to the archive");
        System.out.println("3. Find specific problem details in the archive");
        System.out.println("4. Edit an existing problem in the archive");
        System.out.println("5. Delete a problem from the archive");
        System.out.println("0. Exit");
        System.out.print("Select an action: ");
    }

    private void retrieveAll() {
        try {
            var problems = service.retrieveAll();
            if (problems.isEmpty()) {
                System.out.println("There are no problems available in the archive.");
            } else {
                System.out.println("List of problems found in the archive:");
                problems.forEach(System.out::println);
            }
        } catch (DataAccessException e) {
            System.err.println("Something went wrong while retrieving the problem list. Please try again.");
            logger.error("Failed to retrieve the list of problems", e);
        }
    }

    private void create() {
        try {
            System.out.print("Enter title for the new problem: ");
            String title = scanner.nextLine();

            System.out.print("Enter problem description: ");
            String description = scanner.nextLine();

            System.out.print("Specify memory limit (MB): ");
            int memoryLimit = Integer.parseInt(scanner.nextLine());

            System.out.print("Specify execution time limit (ms): ");
            int timeLimit = Integer.parseInt(scanner.nextLine());

            service.create(new Problem(null, title, description, memoryLimit, timeLimit, null));
            System.out.println("Problem added successfully.");
        } catch (NumberFormatException e) {
            System.err.println("Input error: memory and time limits must be integers.");
        } catch (Exception e) {
            System.err.println("Something went wrong while adding the problem. Operation failed, please try again.");
            logger.error("Failed to add problem.", e);
        }
    }

    private void retrieveById() {
        try {
            System.out.print("Enter the id of the problem you want to find: ");
            int id = Integer.parseInt(scanner.nextLine());

            Problem problem = service.retrieveById(id);
            System.out.println("Detailed problem information:");
            System.out.println(problem);
        } catch (EntityNotFoundException e) {
            System.out.println("Search error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Input error: problem id must be a numeric value.");
        } catch (Exception e) {
            System.err.println("Something went wrong during the search. Please try again.");
            logger.error("Failed to find problem", e);
        }
    }

    private void update() {
        try {
            System.out.print("Enter the id of the problem you want to edit: ");
            int id = Integer.parseInt(scanner.nextLine());

            Problem existing = service.retrieveById(id);

            System.out.print("Enter new title (current: " + existing.getTitle() + "): ");
            String title = scanner.nextLine();

            System.out.print("Enter new description (current: " + existing.getDescription() + "): ");
            String description = scanner.nextLine();

            System.out.print("Enter new memory limit in MB (current: " + existing.getMemoryLimitMb() + "): ");
            int memoryLimit = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter new time limit in ms (current: " + existing.getTimeLimitMs() + "): ");
            int timeLimit = Integer.parseInt(scanner.nextLine());

            existing.setTitle(title);
            existing.setDescription(description);
            existing.setMemoryLimitMb(memoryLimit);
            existing.setTimeLimitMs(timeLimit);

            service.update(existing);

            System.out.println("Problem information updated successfully.");
        } catch (EntityNotFoundException e) {
            System.out.println("Update error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Input error: id and limits must be numeric values.");
        } catch (Exception e) {
            System.err.println("Something went wrong while updating the problem. Operation failed, please try again.");
            logger.error("Failed to update problem", e);
        }
    }

    private void delete() {
        try {
            System.out.print("Enter the id of the problem you want to delete: ");

            int id = Integer.parseInt(scanner.nextLine());
            service.delete(id);

            System.out.println("Problem deleted successfully.");
        } catch (EntityNotFoundException e) {
            System.out.println("Deletion error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Something went wrong while deleting the problem. Operation failed, please try again.");
            logger.error("Failed to delete problem.", e);
        }
    }
}