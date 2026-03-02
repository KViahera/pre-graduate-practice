package dao;

import exception.DataAccessException;
import model.Problem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProblemDaoImplementation implements ProblemDao {
    private static final String CREATE_SQL = "INSERT INTO problems (title, description, memory_limit_mb, time_limit_ms) VALUES (?, ?, ?, ?)";
    private static final String RETRIEVE_ALL_SQL = "SELECT * FROM problems";
    private static final String RETRIEVE_BY_ID_SQL = "SELECT * FROM problems WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE problems SET title = ?, description = ?, memory_limit_mb = ?, time_limit_ms = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM problems WHERE id = ?";

    private final Connection connection;

    public ProblemDaoImplementation(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Problem problem) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, problem.getTitle());
            statement.setString(2, problem.getDescription());
            statement.setInt(3, problem.getMemoryLimitMb());
            statement.setInt(4, problem.getTimeLimitMs());

            statement.executeUpdate();
            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    problem.setId(result.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add a new problem to the archive.", e);
        }
    }

    @Override
    public List<Problem> retrieveAll() {
        List<Problem> problems = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(RETRIEVE_ALL_SQL)) {
            while (result.next()) {
                problems.add(mapRowToProblem(result));
            }
            return problems;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve the complete list of problems from the archive.", e);
        }
    }

    @Override
    public Optional<Problem> retrieveById(int id) {
        try (PreparedStatement statement = connection.prepareStatement(RETRIEVE_BY_ID_SQL)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRowToProblem(result));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve data for the problem with ID " + id + " from the archive.", e);
        }
    }

    @Override
    public void update(Problem problem) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, problem.getTitle());
            statement.setString(2, problem.getDescription());
            statement.setInt(3, problem.getMemoryLimitMb());
            statement.setInt(4, problem.getTimeLimitMs());
            statement.setInt(5, problem.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update data for the problem with ID " + problem.getId() + " in the archive.", e);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete the problem with ID " + id + " from the archive.", e);
        }
    }

    private Problem mapRowToProblem(ResultSet result) throws SQLException {
        var createdAt = result.getTimestamp("created_at");
        return new Problem(
                result.getInt("id"),
                result.getString("title"),
                result.getString("description"),
                result.getInt("memory_limit_mb"),
                result.getInt("time_limit_ms"),
                createdAt != null ? createdAt.toLocalDateTime() : null
        );
    }
}