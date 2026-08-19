package com.example.fitnesstrackingapp.repository;

import com.example.fitnesstrackingapp.model.PlanExercise;
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
 * Upravlja vežbama koje pripadaju planovima treninga.
 */
public class PlanExerciseRepository
        implements CrudRepository<PlanExercise> {

    private static final String INSERT_SQL = """
            INSERT INTO plan_exercises
                (plan_id, exercise_id, target_sets,
                 target_reps, target_weight, position)
            VALUES (?, ?, ?, ?, ?, ?)
            """;


    private static final String FIND_BY_ID_SQL = """
            SELECT id, plan_id, exercise_id, target_sets,
                   target_reps, target_weight, position
            FROM plan_exercises
            WHERE id = ?
            """;


    private static final String FIND_ALL_SQL = """
            SELECT id, plan_id, exercise_id, target_sets,
                   target_reps, target_weight, position
            FROM plan_exercises
            ORDER BY plan_id, position
            """;


    private static final String FIND_BY_PLAN_SQL = """
            SELECT id, plan_id, exercise_id, target_sets,
                   target_reps, target_weight, position
            FROM plan_exercises
            WHERE plan_id = ?
            ORDER BY position
            """;


    private static final String UPDATE_SQL = """
            UPDATE plan_exercises
            SET plan_id = ?, exercise_id = ?,
                target_sets = ?, target_reps = ?,
                target_weight = ?, position = ?
            WHERE id = ?
            """;


    private static final String DELETE_SQL = """
            DELETE FROM plan_exercises
            WHERE id = ?
            """;


    @Override
    public PlanExercise create(PlanExercise planExercise)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            setParameters(statement, planExercise);
            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    planExercise.setId(
                            generatedKeys.getInt(1)
                    );
                } else {
                    throw new SQLException(
                            "Baza nije vratila ID stavke plana."
                    );
                }
            }
        }

        return planExercise;
    }


    @Override
    public Optional<PlanExercise> findById(int id)
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
    public List<PlanExercise> findAll()
            throws SQLException {

        List<PlanExercise> items = new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ALL_SQL
                     );
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                items.add(mapRow(resultSet));
            }
        }

        return items;
    }


    public List<PlanExercise> findByPlanId(int planId)
            throws SQLException {

        List<PlanExercise> items = new ArrayList<>();

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_PLAN_SQL
                     )) {

            statement.setInt(1, planId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    items.add(mapRow(resultSet));
                }
            }
        }

        return items;
    }


    @Override
    public boolean update(PlanExercise planExercise)
            throws SQLException {

        try (Connection connection =
                     DatabaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SQL
                     )) {

            setParameters(statement, planExercise);
            statement.setInt(7, planExercise.getId());

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
            PlanExercise item
    ) throws SQLException {

        statement.setInt(1, item.getPlanId());
        statement.setInt(2, item.getExerciseId());
        statement.setInt(3, item.getTargetSets());
        statement.setInt(4, item.getTargetReps());
        statement.setDouble(5, item.getTargetWeight());
        statement.setInt(6, item.getPosition());
    }


    private PlanExercise mapRow(ResultSet resultSet)
            throws SQLException {

        return new PlanExercise(
                resultSet.getInt("id"),
                resultSet.getInt("plan_id"),
                resultSet.getInt("exercise_id"),
                resultSet.getInt("target_sets"),
                resultSet.getInt("target_reps"),
                resultSet.getDouble("target_weight"),
                resultSet.getInt("position")
        );
    }
}