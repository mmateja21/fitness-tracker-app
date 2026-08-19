package com.example.fitnesstrackingapp.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Sadrži zbirne podatke o treninzima korisnika.
 */
public class WorkoutSummary implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Ukupan broj održanih treninga. */
    private int totalWorkouts;

    /** Ukupno trajanje svih treninga u minutima. */
    private int totalDurationMinutes;

    /** Prosečno trajanje treninga u minutima. */
    private double averageDurationMinutes;

    public WorkoutSummary() {
    }

    public WorkoutSummary(
            int totalWorkouts,
            int totalDurationMinutes,
            double averageDurationMinutes
    ) {
        this.totalWorkouts = totalWorkouts;
        this.totalDurationMinutes = totalDurationMinutes;
        this.averageDurationMinutes = averageDurationMinutes;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(
            int totalDurationMinutes
    ) {
        this.totalDurationMinutes =
                totalDurationMinutes;
    }

    public double getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    public void setAverageDurationMinutes(
            double averageDurationMinutes
    ) {
        this.averageDurationMinutes =
                averageDurationMinutes;
    }
}