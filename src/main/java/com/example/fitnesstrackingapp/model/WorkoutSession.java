package com.example.fitnesstrackingapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Predstavlja jedan održani trening korisnika.
 */
public class WorkoutSession implements Serializable {

    /** Verzija klase koja se koristi pri serijalizaciji. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator treninga. */
    private int id;

    /** Identifikator korisnika. */
    private int userId;

    /** ID korišćenog plana ili null za trening bez plana. */
    private Integer planId;

    /** Datum održavanja treninga. */
    private LocalDate workoutDate;

    /** Trajanje treninga u minutima. */
    private int durationMinutes;

    /** Dodatne napomene. */
    private String notes;


    public WorkoutSession() {
    }



    public WorkoutSession(
            int userId,
            Integer planId,
            LocalDate workoutDate,
            int durationMinutes,
            String notes
    ) {
        this.userId = userId;
        this.planId = planId;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }


    public WorkoutSession(
            int id,
            int userId,
            Integer planId,
            LocalDate workoutDate,
            int durationMinutes,
            String notes
    ) {
        this.id = id;
        this.userId = userId;
        this.planId = planId;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }


    public Integer getPlanId() {
        return planId;
    }


    public void setPlanId(Integer planId) {
        this.planId = planId;
    }


    public LocalDate getWorkoutDate() {
        return workoutDate;
    }


    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }


    public int getDurationMinutes() {
        return durationMinutes;
    }


    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }


    public String getNotes() {
        return notes;
    }


    public void setNotes(String notes) {
        this.notes = notes;
    }
}