package com.example.fitnesstrackingapp.repository;

import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Izvršava operacije nad održanim treninzima u SQLite bazi.
 */
public class WorkoutSessionRepository
        implements CrudRepository<WorkoutSession> {

    private static final String INSERT_SQL = """
            INSERT INTO workout_sessions
                (user_id, plan_id, workout_date,
                 duration_minutes, notes)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, user_id, plan_id, workout_date,
                   duration_minutes, notes
            FROM workout_sessions
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, user_id, plan_id, workout_date,
                   duration_minutes, notes
            FROM workout_sessions
            ORDER BY workout_date DESC, id DESC
            """;

    private static final String FIND_BY_USER_SQL = """
            SELECT id, user_id, plan_id, workout_date,
                   duration_minutes, notes
            FROM workout_sessions
            WHERE user_id = ?
            ORDER BY workout_date DESC, id DESC
            """;

    private static final String UPDATE_SQL = """
            UPDATE workout_sessions
            SET user_id = ?,
                plan_id = ?,
                workout_date = ?,
                duration_minutes = ?,
                notes = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM workout_sessions
            WHERE id = ?
            """;

    @Override
    public WorkoutSession create(WorkoutSession session)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            setSessionParameters(statement, session);
            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    session.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Baza nije vratila ID novog treninga."
                    );
                }
            }
        }

        return findById(session.getId())
                .orElseThrow(() -> new SQLException(
                        "Sačuvani trening nije moguće učitati."
                ));
    }

    @Override
    public Optional<WorkoutSession> findById(int id)
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
    public List<WorkoutSession> findAll()
            throws SQLException {

        List<WorkoutSession> sessions = new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ALL_SQL
                     );
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                sessions.add(mapRow(resultSet));
            }
        }

        return sessions;
    }

    /**
     * Vraća održane treninge određenog korisnika.
     *
     * @param userId identifikator korisnika
     * @return održani treninzi korisnika
     * @throws SQLException ako čitanje baze ne uspe
     */
    public List<WorkoutSession> findByUserId(int userId)
            throws SQLException {

        List<WorkoutSession> sessions = new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_USER_SQL
                     )) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    sessions.add(mapRow(resultSet));
                }
            }
        }

        return sessions;
    }

    @Override
    public boolean update(WorkoutSession session)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SQL
                     )) {

            setSessionParameters(statement, session);
            statement.setInt(6, session.getId());

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

    /**
     * Postavlja parametre za INSERT i UPDATE naredbe.
     */
    private void setSessionParameters(
            PreparedStatement statement,
            WorkoutSession session
    ) throws SQLException {

        statement.setInt(1, session.getUserId());

        if (session.getPlanId() == null) {
            statement.setNull(2, Types.INTEGER);
        } else {
            statement.setInt(2, session.getPlanId());
        }

        statement.setString(
                3,
                session.getWorkoutDate().toString()
        );
        statement.setInt(
                4,
                session.getDurationMinutes()
        );
        statement.setString(5, session.getNotes());
    }

    /**
     * Pretvara red iz baze u WorkoutSession objekat.
     */
    private WorkoutSession mapRow(ResultSet resultSet)
            throws SQLException {

        int planIdValue = resultSet.getInt("plan_id");
        Integer planId = resultSet.wasNull()
                ? null
                : planIdValue;

        return new WorkoutSession(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                planId,
                LocalDate.parse(
                        resultSet.getString("workout_date")
                ),
                resultSet.getInt("duration_minutes"),
                resultSet.getString("notes")
        );
    }
}