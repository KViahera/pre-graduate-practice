package dao;

import model.Problem;
import java.util.List;
import java.util.Optional;

public interface ProblemDao {
    void create(Problem problem);

    List<Problem> retrieveAll();
    Optional<Problem> retrieveById(int id);

    void update(Problem problem);

    void delete(int id);
}