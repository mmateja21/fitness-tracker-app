package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.MuscleGroup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


 //Testira validaciju ExerciseService klase.

class ExerciseServiceTest {


    //Proverava da servis odbija prazan naziv vežbe.

    @Test
    void createExerciseWithBlankNameThrowsValidationException() {
        ExerciseService service = new ExerciseService();

        Exercise exercise = new Exercise(
                "   ",
                MuscleGroup.CHEST,
                "Barbell",
                "Test opis"
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createExercise(exercise)
        );

        assertEquals(
                "Naziv vežbe je obavezan.",
                exception.getMessage()
        );
    }

      //Proverava da servis odbija naziv kraći od dva znaka.

    @Test
    void createExerciseWithShortNameThrowsValidationException() {
        ExerciseService service = new ExerciseService();

        Exercise exercise = new Exercise(
                "A",
                MuscleGroup.ARMS,
                "",
                ""
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createExercise(exercise)
        );

        assertEquals(
                "Naziv vežbe mora imati najmanje 2 znaka.",
                exception.getMessage()
        );
    }


    // Proverava da servis zahteva mišićnu grupu.

    @Test
    void createExerciseWithoutMuscleGroupThrowsValidationException() {
        ExerciseService service = new ExerciseService();

        Exercise exercise = new Exercise(
                "Bench Press",
                null,
                "Barbell",
                ""
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createExercise(exercise)
        );

        assertEquals(
                "Mišićna grupa je obavezna.",
                exception.getMessage()
        );
    }
}