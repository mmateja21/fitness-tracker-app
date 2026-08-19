package com.example.fitnesstrackingapp.repository;

import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Izvršava operacije nad planovima treninga u SQLite bazi.
 */
public class WorkoutPlanRepository
        implements CrudRepository<WorkoutPlan> {

    /** Format datuma koji koristi SQLite CURRENT_TIMESTAMP. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
            );

    /** SQL za dodavanje plana. */
    private static final String INSERT_SQL = """
            INSERT INTO workout_plans
                (user_id, name, description)
            VALUES (?, ?, ?)
            """;

    /** SQL za pronalaženje plana prema ID-u. */
    private static final String FIND_BY_ID_SQL = """
            SELECT id, user_id, name, description, created_at
            FROM workout_plans
            WHERE id = ?
            """;

    /** SQL za učitavanje svih planova. */
    private static final String FIND_ALL_SQL = """
            SELECT id, user_id, name, description, created_at
            FROM workout_plans
            ORDER BY created_at DESC
            """;

    /** SQL za učitavanje planova jednog korisnika. */
    private static final String FIND_BY_USER_SQL = """
            SELECT id, user_id, name, description, created_at
            FROM workout_plans
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;

    /** SQL za ažuriranje plana. */
    private static final String UPDATE_SQL = """
            UPDATE workout_plans
            SET user_id = ?, name = ?, description = ?
            WHERE id = ?
            """;

    /** SQL za brisanje plana. */
    private static final String DELETE_SQL = """
            DELETE FROM workout_plans
            WHERE id = ?
            """;


    @Override
    public WorkoutPlan create(WorkoutPlan plan)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            setPlanParameters(statement, plan);
            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    plan.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Baza nije vratila ID novog plana."
                    );
                }
            }
        }

        return findById(plan.getId())
                .orElseThrow(() -> new SQLException(
                        "Sačuvani plan nije moguće ponovo učitati."
                ));
    }


    @Override
    public Optional<WorkoutPlan> findById(int id)
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
                    return Optional.of(
                            mapRow(resultSet)
                    );
                }

                return Optional.empty();
            }
        }
    }


    @Override
    public List<WorkoutPlan> findAll()
            throws SQLException {

        List<WorkoutPlan> plans = new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ALL_SQL
                     );
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                plans.add(mapRow(resultSet));
            }
        }

        return plans;
    }

    /**
     * Vraća sve planove određenog korisnika.
     *
     * @param userId identifikator korisnika
     * @return planovi korisnika
     * @throws SQLException ako čitanje baze ne uspe
     */
    public List<WorkoutPlan> findByUserId(int userId)
            throws SQLException {

        List<WorkoutPlan> plans = new ArrayList<>();

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
                    plans.add(mapRow(resultSet));
                }
            }
        }

        return plans;
    }


    @Override
    public boolean update(WorkoutPlan plan)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SQL
                     )) {

            setPlanParameters(statement, plan);
            statement.setInt(4, plan.getId());

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
     * Postavlja zajedničke parametre INSERT i UPDATE naredbi.
     *
     * @param statement pripremljena SQL naredba
     * @param plan plan čiji se podaci postavljaju
     * @throws SQLException ako postavljanje parametara ne uspe
     */
    private void setPlanParameters(
            PreparedStatement statement,
            WorkoutPlan plan
    ) throws SQLException {

        statement.setInt(1, plan.getUserId());
        statement.setString(2, plan.getName());
        statement.setString(3, plan.getDescription());
    }

    /**
     * Pretvara trenutni red rezultata u WorkoutPlan objekat.
     *
     * @param resultSet rezultat SQL upita
     * @return plan napravljen iz trenutnog reda
     * @throws SQLException ako čitanje podataka ne uspe
     */
    private WorkoutPlan mapRow(ResultSet resultSet)
            throws SQLException {

        LocalDateTime createdAt = LocalDateTime.parse(
                resultSet.getString("created_at"),
                DATE_TIME_FORMATTER
        );

        return new WorkoutPlan(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                createdAt
        );
    }
}
