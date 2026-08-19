package com.example.fitnesstrackingapp.model;

import java.io.Serializable;

public class SessionExercise implements Serializable {

    private int id;
    private int sessionId;
    private int exerciseId;
    private int completedSets;
    private int completedReps;
    private double weight;

    public SessionExercise() {
    }

    public SessionExercise(
            int sessionId,
            int exerciseId,
            int completedSets,
            int completedReps,
            double weight
    ) {
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.completedSets = completedSets;
        this.completedReps = completedReps;
        this.weight = weight;
    }

    public SessionExercise(
            int id,
            int sessionId,
            int exerciseId,
            int completedSets,
            int completedReps,
            double weight
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.completedSets = completedSets;
        this.completedReps = completedReps;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getCompletedSets() {
        return completedSets;
    }

    public void setCompletedSets(int completedSets) {
        this.completedSets = completedSets;
    }

    public int getCompletedReps() {
        return completedReps;
    }

    public void setCompletedReps(int completedReps) {
        this.completedReps = completedReps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}