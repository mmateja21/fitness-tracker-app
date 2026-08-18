package com.example.fitnesstrackingapp.repository;
import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.MuscleGroup;
import com.example.fitnesstrackingapp.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseRepository implements CrudRepository<Exercise> {

    private static final String INSERT_SQL = """
            
            INSERT INTO exercises (name, muscle_group, equipment, description)
            VALUES (?, ?, ?, ?)

""";

    private static final String FIND_BY_ID_SQL = """
            SELECT id, name, muscle_group, equipment, description
            FROM exercises
            WHERE id=?""";


    private static final String FIND_ALL_SQL= """
            SELECT id, name, muscle_group, equipment, description
            FROM exercises
            ORDER BY name
            """;

    private static final String UPDATE_SQL = """
            UPDATE exercises
            SET name = ?, muscle_group= ?, equipment=?, description=?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM exercises
            WHERE id = ?
            """;
@Override
    public Exercise create(Exercise exercise) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {

            setExerciseParameters(statement, exercise);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exercise.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Baza nije vratila id nove vezbe. "
                    );
                }
            }
            return exercise;
        }}

    @Override
             public Optional <Exercise> findById(int id) throws SQLException{
        try(Connection connection= DatabaseManager.getConnection();
            PreparedStatement statement=connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        }}

    @Override
    public List<Exercise> findAll() throws SQLException{
        List <Exercise> exercises = new ArrayList<>();

        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(FIND_ALL_SQL);
            ResultSet resultSet = statement.executeQuery()){

            while (resultSet.next()){
                exercises.add(mapRow(resultSet));
            }


        }
        return exercises;
    }

    @Override
    public boolean update(Exercise exercise) throws SQLException{
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement=
                    connection.prepareStatement(UPDATE_SQL)) {

            setExerciseParameters(statement, exercise);
            statement.setInt(5, exercise.getId());
            return statement.executeUpdate() == 1;
        }}

    @Override
    public boolean deleteById(int id) throws SQLException{
        try(Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement=
                    connection.prepareStatement(DELETE_SQL)){
            statement.setInt(1, id);
            return statement.executeUpdate()== 1;
        }
    }

    private void setExerciseParameters(PreparedStatement statement, Exercise exercise
    ) throws SQLException {
        statement.setString(1, exercise.getName());
        statement.setString(2, exercise.getMuscleGroup().name());
        statement.setString(3, exercise.getEquipment());
        statement.setString(4, exercise.getDescription());
    }
    private Exercise mapRow(ResultSet resultSet) throws SQLException {
        return new Exercise(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                MuscleGroup.valueOf(resultSet.getString("muscle_group")),
                resultSet.getString("equipment"),
                resultSet.getString("description"));

    }
        }


