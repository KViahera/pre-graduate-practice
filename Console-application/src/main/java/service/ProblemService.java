package service;

import dao.ProblemDao;
import exception.EntityNotFoundException;
import model.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);
    private final ProblemDao dao;

    public ProblemService(ProblemDao problemDao) {
        this.dao = problemDao;
    }

    public void create(Problem problem) {
        validate(problem);

        logger.info("Adding a new problem to the archive: {}", problem);
        dao.create(problem);
    }

    public List<Problem> retrieveAll() {
        logger.info("Retrieving the full list of problems from the archive");
        return dao.retrieveAll();
    }

    public Problem retrieveById(int id) {
        logger.info("Searching for a problem in the archive by id: {}", id);

        return dao.retrieveById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempted to access a non-existent problem with id: {}", id);
                    return new EntityNotFoundException("Problem with the specified id " + id + " was not found in the archive.");
                });
    }

    public void update(Problem problem) {
        if (problem.getId() == null) {
            throw new IllegalArgumentException("Problem id is required to perform the update operation.");
        }

        retrieveById(problem.getId());
        validate(problem);

        logger.info("Updating problem data in the archive for id: {}", problem.getId());
        dao.update(problem);
    }

    public void delete(int id) {
        retrieveById(id);

        logger.warn("Deleting problem from the archive with id: {}", id);
        dao.delete(id);
    }

    private void validate(Problem p) {
        if (p.getTitle() == null || p.getTitle().isBlank()) {
            throw new IllegalArgumentException("Problem title cannot be empty or consist only of whitespace.");
        }
        if (p.getMemoryLimitMb() <= 0) {
            throw new IllegalArgumentException("Memory limit must be a positive number.");
        }
        if (p.getTimeLimitMs() <= 0) {
            throw new IllegalArgumentException("Execution time limit must be a positive number.");
        }
    }
}