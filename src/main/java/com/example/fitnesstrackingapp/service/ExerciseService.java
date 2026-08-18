package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.repository.CrudRepository;
import com.example.fitnesstrackingapp.repository.ExerciseRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class ExerciseService {


    private final CrudRepository<Exercise> exerciseRepository;


    public ExerciseService() {
        this(new ExerciseRepository());
    }


    public ExerciseService(
            CrudRepository<Exercise> exerciseRepository
    ) {
        this.exerciseRepository = Objects.requireNonNull(
                exerciseRepository,
                "Exercise repository ne sme biti null."
        );
    }


    public Exercise createExercise(Exercise exercise)
            throws ValidationException, SQLException {

        validateExercise(exercise);
        normalizeExercise(exercise);
        ensureNameIsUnique(exercise.getName(), 0);

        return exerciseRepository.create(exercise);
    }


    public List<Exercise> getAllExercises() throws SQLException {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(int id)
        throws ValidationException, EntityNotFoundException, SQLException{
        validateId(id);
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vezba sa ID " + id + " nije pronadjena."
                ));
    }

    public Exercise updateExercise(Exercise exercise)
        throws ValidationException, EntityNotFoundException, SQLException{

        validateExercise(exercise);
        validateId(exercise.getId());
        normalizeExercise(exercise);
        ensureNameIsUnique(exercise.getName(),exercise.getId());

        boolean updated = exerciseRepository.update(exercise);

        if(!updated){
            throw new EntityNotFoundException(
                    "Vezba sa ID " + exercise.getId() + " nije pronadjena."
            );
        }
        return exercise;
    }

    public void deleteExercise(int id)
        throws ValidationException,EntityNotFoundException, SQLException{

        validateId(id);
        boolean deleted = exerciseRepository.deleteById(id);

        if(!deleted){
            throw new EntityNotFoundException(
                    "Vezba sa ID " + id + " nije pronadjena."
            );
        }
    }

    private void validateId(int id) throws ValidationException{
        if(id <=0){
            throw new ValidationException(
                    "ID vezbe mora biti pozitivan broj."
            );
        }
    }


    private void validateExercise(Exercise exercise)
            throws ValidationException {

        if (exercise == null) {
            throw new ValidationException(
                    "Podaci o vežbi nisu prosleđeni."
            );
        }

        if (exercise.getName() == null
                || exercise.getName().isBlank()) {
            throw new ValidationException(
                    "Naziv vežbe je obavezan."
            );
        }

        String trimmedName = exercise.getName().trim();

        if (trimmedName.length() < 2) {
            throw new ValidationException(
                    "Naziv vežbe mora imati najmanje 2 znaka."
            );
        }

        if (trimmedName.length() > 100) {
            throw new ValidationException(
                    "Naziv vežbe može imati najviše 100 znakova."
            );
        }

        if (exercise.getMuscleGroup() == null) {
            throw new ValidationException(
                    "Mišićna grupa je obavezna."
            );
        }

        if (exercise.getEquipment() != null
                && exercise.getEquipment().trim().length() > 100) {
            throw new ValidationException(
                    "Naziv opreme može imati najviše 100 znakova."
            );
        }

        if (exercise.getDescription() != null
                && exercise.getDescription().trim().length() > 500) {
            throw new ValidationException(
                    "Opis može imati najviše 500 znakova."
            );
        }
    }



    private void normalizeExercise(Exercise exercise) {
        exercise.setName(
                exercise.getName().trim().replaceAll("\\s+", " ")
        );

        exercise.setEquipment(
                normalizeOptionalText(exercise.getEquipment())
        );

        exercise.setDescription(
                normalizeOptionalText(exercise.getDescription())
        );
    }


    private String normalizeOptionalText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ");
    }


    private void ensureNameIsUnique(String name, int excludedId)
            throws ValidationException, SQLException {

        boolean duplicateExists = exerciseRepository.findAll()
                .stream()
                .anyMatch(existingExercise ->
                        existingExercise.getId() != excludedId
                                && existingExercise.getName()
                                .equalsIgnoreCase(name)
                );

        if (duplicateExists) {
            throw new ValidationException(
                    "Vežba sa tim nazivom već postoji."
            );
        }
    }
}