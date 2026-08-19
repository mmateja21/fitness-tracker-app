package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.SessionExercise;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testovi poslovne logike odrađenih vežbi.
 */
class SessionExerciseServiceTest {

    private final SessionExerciseService service =
            new SessionExerciseService();

    @Test
    void calculateVolumeReturnsCorrectResult()
            throws ValidationException {

        SessionExercise exercise = new SessionExercise(
                1,
                1,
                4,
                8,
                70.0
        );

        double volume =
                service.calculateVolume(exercise);

        assertEquals(
                2240.0,
                volume,
                0.001
        );
    }

    @Test
    void calculateTotalVolumeReturnsSumOfExercises()
            throws ValidationException {

        SessionExercise firstExercise =
                new SessionExercise(
                        1,
                        1,
                        4,
                        8,
                        70.0
                );

        SessionExercise secondExercise =
                new SessionExercise(
                        1,
                        2,
                        3,
                        10,
                        20.0
                );

        double totalVolume =
                service.calculateTotalVolume(
                        List.of(
                                firstExercise,
                                secondExercise
                        )
                );

        assertEquals(
                2840.0,
                totalVolume,
                0.001
        );
    }

    @Test
    void negativeWeightThrowsValidationException() {
        SessionExercise exercise = new SessionExercise(
                1,
                1,
                3,
                10,
                -5.0
        );

        assertThrows(
                ValidationException.class,
                () -> service.calculateVolume(exercise)
        );
    }
}