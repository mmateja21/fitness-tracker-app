package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.repository.WorkoutPlanRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Sadrži poslovnu logiku i validaciju planova treninga.
 */
public class WorkoutPlanService {

    /** ID podrazumevanog lokalnog korisnika. */
    public static final int DEFAULT_USER_ID = 1;

    /** Repository za rad sa planovima treninga. */
    private final WorkoutPlanRepository planRepository;

    /**
     * Pravi servis sa standardnim SQLite repository objektom.
     */
    public WorkoutPlanService() {
        this(new WorkoutPlanRepository());
    }

    /**
     * Pravi servis sa prosleđenim repository objektom.
     *
     * @param planRepository repository za planove
     */
    public WorkoutPlanService(
            WorkoutPlanRepository planRepository
    ) {
        this.planRepository = Objects.requireNonNull(
                planRepository,
                "WorkoutPlanRepository ne sme biti null."
        );
    }

    /**
     * Validira i čuva novi plan treninga.
     *
     * @param plan plan koji se čuva
     * @return sačuvani plan
     * @throws ValidationException ako podaci nisu ispravni
     * @throws SQLException ako upis u bazu ne uspe
     */
    public WorkoutPlan createPlan(WorkoutPlan plan)
            throws ValidationException, SQLException {

        validatePlan(plan);
        normalizePlan(plan);
        ensureNameIsUnique(
                plan.getUserId(),
                plan.getName(),
                0
        );

        return planRepository.create(plan);
    }

    /**
     * Vraća planove podrazumevanog korisnika.
     *
     * @return planovi lokalnog korisnika
     * @throws SQLException ako čitanje baze ne uspe
     */
    public List<WorkoutPlan> getPlansForDefaultUser()
            throws SQLException {

        return planRepository.findByUserId(
                DEFAULT_USER_ID
        );
    }


    public WorkoutPlan getPlanById(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        return planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Plan sa ID-em " + id
                                + " nije pronađen."
                ));
    }

    /**
     * Validira i ažurira plan treninga.
     *
     * @param plan plan sa izmenjenim podacima
     * @return ažurirani plan
     * @throws ValidationException ako podaci nisu ispravni
     * @throws EntityNotFoundException ako plan ne postoji
     * @throws SQLException ako izmena baze ne uspe
     */
    public WorkoutPlan updatePlan(WorkoutPlan plan)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validatePlan(plan);
        validateId(plan.getId());
        normalizePlan(plan);

        ensureNameIsUnique(
                plan.getUserId(),
                plan.getName(),
                plan.getId()
        );

        if (!planRepository.update(plan)) {
            throw new EntityNotFoundException(
                    "Plan sa ID-em " + plan.getId()
                            + " nije pronađen."
            );
        }

        return planRepository.findById(plan.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ažurirani plan nije pronađen."
                ));
    }

    /**
     * Briše plan i njegove stavke.
     *
     * @param id identifikator plana
     * @throws ValidationException ako ID nije ispravan
     * @throws EntityNotFoundException ako plan ne postoji
     * @throws SQLException ako brisanje ne uspe
     */
    public void deletePlan(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        if (!planRepository.deleteById(id)) {
            throw new EntityNotFoundException(
                    "Plan sa ID-em " + id
                            + " nije pronađen."
            );
        }
    }

    /**
     * Proverava podatke plana.
     */
    private void validatePlan(WorkoutPlan plan)
            throws ValidationException {

        if (plan == null) {
            throw new ValidationException(
                    "Podaci o planu nisu prosleđeni."
            );
        }

        if (plan.getUserId() <= 0) {
            throw new ValidationException(
                    "Korisnik plana nije ispravan."
            );
        }

        if (plan.getName() == null
                || plan.getName().isBlank()) {
            throw new ValidationException(
                    "Naziv plana je obavezan."
            );
        }

        String trimmedName = plan.getName().trim();

        if (trimmedName.length() < 2) {
            throw new ValidationException(
                    "Naziv plana mora imati najmanje 2 znaka."
            );
        }

        if (trimmedName.length() > 100) {
            throw new ValidationException(
                    "Naziv plana može imati najviše 100 znakova."
            );
        }

        if (plan.getDescription() != null
                && plan.getDescription().trim().length() > 500) {
            throw new ValidationException(
                    "Opis plana može imati najviše 500 znakova."
            );
        }
    }

    /**
     * Normalizuje tekstualne podatke plana.

     */
    private void normalizePlan(WorkoutPlan plan) {
        plan.setName(
                plan.getName().trim().replaceAll("\\s+", " ")
        );

        if (plan.getDescription() == null) {
            plan.setDescription("");
        } else {
            plan.setDescription(
                    plan.getDescription()
                            .trim()
                            .replaceAll("\\s+", " ")
            );
        }
    }



    private void ensureNameIsUnique(
            int userId,
            String name,
            int excludedId
    ) throws ValidationException, SQLException {

        boolean duplicateExists =
                planRepository.findByUserId(userId)
                        .stream()
                        .anyMatch(existingPlan ->
                                existingPlan.getId() != excludedId
                                        && existingPlan.getName()
                                        .equalsIgnoreCase(name)
                        );

        if (duplicateExists) {
            throw new ValidationException(
                    "Plan sa tim nazivom već postoji."
            );
        }
    }


    private void validateId(int id)
            throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(
                    "ID plana mora biti pozitivan broj."
            );
        }
    }
}