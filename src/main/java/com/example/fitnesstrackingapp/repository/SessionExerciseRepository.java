package com.example.fitnesstrackingapp.repository;

import com.example.fitnesstrackingapp.model.SessionExercise;
import com.example.fitnesstrackingapp.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Izvršava operacije nad vežbama odrađenim tokom treninga.
 */
public class SessionExerciseRepository
        implements CrudRepository<SessionExercise> {

    private static final String INSERT_SQL = """
            INSERT INTO session_exercises
                (session_id, exercise_id, completed_sets,
                 completed_reps, weight)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, session_id, exercise_id,
                   completed_sets, completed_reps, weight
            FROM session_exercises
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, session_id, exercise_id,
                   completed_sets, completed_reps, weight
            FROM session_exercises
            ORDER BY id DESC
            """;

    private static final String FIND_BY_SESSION_SQL = """
            SELECT id, session_id, exercise_id,
                   completed_sets, completed_reps, weight
            FROM session_exercises
            WHERE session_id = ?
            ORDER BY id
            """;

    private static final String UPDATE_SQL = """
            UPDATE session_exercises
            SET session_id = ?,
                exercise_id = ?,
                completed_sets = ?,
                completed_reps = ?,
                weight = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM session_exercises
            WHERE id = ?
            """;

    @Override
    public SessionExercise create(SessionExercise sessionExercise)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            setParameters(statement, sessionExercise);
            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    sessionExercise.setId(
                            generatedKeys.getInt(1)
                    );
                } else {
                    throw new SQLException(
                            "Baza nije vratila ID odrađene vežbe."
                    );
                }
            }
        }

        return findById(sessionExercise.getId())
                .orElseThrow(() -> new SQLException(
                        "Sačuvanu vežbu nije moguće učitati."
                ));
    }

    @Override
    public Optional<SessionExercise> findById(int id)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_ID_SQL
                     )) {

            statement.setInt(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    @Override
    public List<SessionExercise> findAll()
            throws SQLException {

        List<SessionExercise> exercises =
                new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ALL_SQL
                     );
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                exercises.add(mapRow(resultSet));
            }
        }

        return exercises;
    }

    /**
     * Vraća sve odrađene vežbe jednog treninga.
     */
    public List<SessionExercise> findBySessionId(
            int sessionId
    ) throws SQLException {

        List<SessionExercise> exercises =
                new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_SESSION_SQL
                     )) {

            statement.setInt(1, sessionId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    exercises.add(mapRow(resultSet));
                }
            }
        }

        return exercises;
    }

    @Override
    public boolean update(SessionExercise sessionExercise)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SQL
                     )) {

            setParameters(statement, sessionExercise);
            statement.setInt(6, sessionExercise.getId());

            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int id)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             DELETE_SQL
                     )) {

            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void setParameters(
            PreparedStatement statement,
            SessionExercise sessionExercise
    ) throws SQLException {

        statement.setInt(
                1,
                sessionExercise.getSessionId()
        );
        statement.setInt(
                2,
                sessionExercise.getExerciseId()
        );
        statement.setInt(
                3,
                sessionExercise.getCompletedSets()
        );
        statement.setInt(
                4,
                sessionExercise.getCompletedReps()
        );
        statement.setDouble(
                5,
                sessionExercise.getWeight()
        );
    }

    private SessionExercise mapRow(ResultSet resultSet)
            throws SQLException {

        return new SessionExercise(
                resultSet.getInt("id"),
                resultSet.getInt("session_id"),
                resultSet.getInt("exercise_id"),
                resultSet.getInt("completed_sets"),
                resultSet.getInt("completed_reps"),
                resultSet.getDouble("weight")
        );
    }
}