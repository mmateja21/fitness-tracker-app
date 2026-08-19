package com.example.fitnesstrackingapp.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Predstavlja jednu vežbu koja pripada planu treninga.
 */
public class PlanExercise implements Serializable {


    private static final long serialVersionUID = 1L;

    /** Jedinstveni identifikator stavke plana. */
    private int id;

    /** Identifikator plana treninga. */
    private int planId;

    /** Identifikator vežbe. */
    private int exerciseId;

    /** Planirani broj serija. */
    private int targetSets;

    /** Planirani broj ponavljanja. */
    private int targetReps;

    /** Planirana težina u kilogramima. */
    private double targetWeight;

    /** Redosled vežbe u planu. */
    private int position;

    /**
     * Pravi praznu stavku plana.
     */
    public PlanExercise() {
    }

    /**
     * Pravi novu stavku koja još nije sačuvana u bazi.
     */
    public PlanExercise(
            int planId,
            int exerciseId,
            int targetSets,
            int targetReps,
            double targetWeight,
            int position
    ) {
        this.planId = planId;
        this.exerciseId = exerciseId;
        this.targetSets = targetSets;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
        this.position = position;
    }

    /**
     * @param id identifikator stavke
     * @param planId identifikator plana
     * @param exerciseId identifikator vežbe
     * @param targetSets broj serija
     * @param targetReps broj ponavljanja
     * @param targetWeight planirana težina
     * @param position redosled vežbe
     */
    public PlanExercise(
            int id,
            int planId,
            int exerciseId,
            int targetSets,
            int targetReps,
            double targetWeight,
            int position
    ) {
        this.id = id;
        this.planId = planId;
        this.exerciseId = exerciseId;
        this.targetSets = targetSets;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
        this.position = position;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getPlanId() {
        return planId;
    }


    public void setPlanId(int planId) {
        this.planId = planId;
    }


    public int getExerciseId() {
        return exerciseId;
    }


    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    /** @return planirani broj serija */
    public int getTargetSets() {
        return targetSets;
    }

    /** @param targetSets novi broj serija */
    public void setTargetSets(int targetSets) {
        this.targetSets = targetSets;
    }

    /** @return planirani broj ponavljanja */
    public int getTargetReps() {
        return targetReps;
    }

    /** @param targetReps novi broj ponavljanja */
    public void setTargetReps(int targetReps) {
        this.targetReps = targetReps;
    }

    /** @return planirana težina */
    public double getTargetWeight() {
        return targetWeight;
    }

    /** @param targetWeight nova planirana težina */
    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    /** @return redosled vežbe u planu */
    public int getPosition() {
        return position;
    }

    /** @param position novi redosled vežbe */
    public void setPosition(int position) {
        this.position = position;
    }
}