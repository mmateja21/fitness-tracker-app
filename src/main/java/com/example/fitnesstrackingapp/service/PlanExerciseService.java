package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.PlanExercise;
import com.example.fitnesstrackingapp.repository.ExerciseRepository;
import com.example.fitnesstrackingapp.repository.PlanExerciseRepository;
import com.example.fitnesstrackingapp.repository.WorkoutPlanRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Sadrži poslovnu logiku za vežbe unutar planova.
 */
public class PlanExerciseService {

    /** Repository za stavke planova. */
    private final PlanExerciseRepository planExerciseRepository;

    /** Repository za proveru planova. */
    private final WorkoutPlanRepository planRepository;

    /** Repository za proveru vežbi. */
    private final ExerciseRepository exerciseRepository;

    /**
     * Pravi servis sa standardnim repository objektima.
     */
    public PlanExerciseService() {
        this.planExerciseRepository =
                new PlanExerciseRepository();

        this.planRepository =
                new WorkoutPlanRepository();

        this.exerciseRepository =
                new ExerciseRepository();
    }


    public PlanExercise addExerciseToPlan(PlanExercise item)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateItem(item);
        ensureRelatedEntitiesExist(item);
        ensureExerciseIsNotAlreadyInPlan(item);

        return planExerciseRepository.create(item);
    }


    public List<PlanExercise> getItemsForPlan(int planId)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validatePositiveId(
                planId,
                "ID plana mora biti pozitivan broj."
        );

        if (planRepository.findById(planId).isEmpty()) {
            throw new EntityNotFoundException(
                    "Plan sa ID-em " + planId
                            + " nije pronađen."
            );
        }

        return planExerciseRepository.findByPlanId(planId);
    }


    public PlanExercise updatePlanExercise(PlanExercise item)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateItem(item);

        validatePositiveId(
                item.getId(),
                "ID stavke plana mora biti pozitivan broj."
        );

        ensureRelatedEntitiesExist(item);

        if (!planExerciseRepository.update(item)) {
            throw new EntityNotFoundException(
                    "Stavka plana sa ID-em "
                            + item.getId()
                            + " nije pronađena."
            );
        }

        return item;
    }


    public void removeExerciseFromPlan(int itemId)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validatePositiveId(
                itemId,
                "ID stavke plana mora biti pozitivan broj."
        );

        if (!planExerciseRepository.deleteById(itemId)) {
            throw new EntityNotFoundException(
                    "Stavka plana sa ID-em "
                            + itemId
                            + " nije pronađena."
            );
        }
    }


    private void validateItem(PlanExercise item)
            throws ValidationException {

        if (item == null) {
            throw new ValidationException(
                    "Podaci o stavci plana nisu prosleđeni."
            );
        }

        validatePositiveId(
                item.getPlanId(),
                "Plan nije ispravan."
        );

        validatePositiveId(
                item.getExerciseId(),
                "Vežba nije ispravna."
        );

        if (item.getTargetSets() <= 0) {
            throw new ValidationException(
                    "Broj serija mora biti veći od nule."
            );
        }

        if (item.getTargetReps() <= 0) {
            throw new ValidationException(
                    "Broj ponavljanja mora biti veći od nule."
            );
        }

        if (item.getTargetWeight() < 0) {
            throw new ValidationException(
                    "Težina ne može biti negativna."
            );
        }

        if (item.getPosition() <= 0) {
            throw new ValidationException(
                    "Pozicija mora biti veća od nule."
            );
        }
    }


    private void ensureRelatedEntitiesExist(PlanExercise item)
            throws EntityNotFoundException, SQLException {

        if (planRepository.findById(
                item.getPlanId()
        ).isEmpty()) {
            throw new EntityNotFoundException(
                    "Izabrani plan ne postoji."
            );
        }

        if (exerciseRepository.findById(
                item.getExerciseId()
        ).isEmpty()) {
            throw new EntityNotFoundException(
                    "Izabrana vežba ne postoji."
            );
        }
    }


    private void ensureExerciseIsNotAlreadyInPlan(
            PlanExercise item
    ) throws ValidationException, SQLException {

        boolean duplicateExists =
                planExerciseRepository
                        .findByPlanId(item.getPlanId())
                        .stream()
                        .anyMatch(existingItem ->
                                existingItem.getExerciseId()
                                        == item.getExerciseId()
                        );

        if (duplicateExists) {
            throw new ValidationException(
                    "Vežba je već dodata u izabrani plan."
            );
        }
    }


    private void validatePositiveId(
            int id,
            String message
    ) throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(message);
        }
    }
}