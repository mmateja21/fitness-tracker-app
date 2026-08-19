package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.SessionExercise;
import com.example.fitnesstrackingapp.repository.SessionExerciseRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Poslovna logika za vežbe odrađene tokom treninga.
 */
public class SessionExerciseService {

    private final SessionExerciseRepository exerciseRepository;

    public SessionExerciseService() {
        this(new SessionExerciseRepository());
    }

    public SessionExerciseService(
            SessionExerciseRepository exerciseRepository
    ) {
        this.exerciseRepository = Objects.requireNonNull(
                exerciseRepository,
                "SessionExerciseRepository ne sme biti null."
        );
    }

    /**
     * Validira i čuva odrađenu vežbu.
     */
    public SessionExercise createSessionExercise(
            SessionExercise sessionExercise
    ) throws ValidationException, SQLException {

        validateSessionExercise(sessionExercise);
        return exerciseRepository.create(sessionExercise);
    }

    /**
     * Vraća odrađene vežbe jednog treninga.
     */
    public List<SessionExercise> getExercisesForSession(
            int sessionId
    ) throws ValidationException, SQLException {

        validateSessionId(sessionId);
        return exerciseRepository.findBySessionId(sessionId);
    }

    /**
     * Pronalazi odrađenu vežbu prema ID-u.
     */
    public SessionExercise getSessionExerciseById(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        return exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Odrađena vežba sa ID-em " + id
                                + " nije pronađena."
                ));
    }

    /**
     * Validira i ažurira odrađenu vežbu.
     */
    public SessionExercise updateSessionExercise(
            SessionExercise sessionExercise
    ) throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateSessionExercise(sessionExercise);
        validateId(sessionExercise.getId());

        if (!exerciseRepository.update(sessionExercise)) {
            throw new EntityNotFoundException(
                    "Odrađena vežba sa ID-em "
                            + sessionExercise.getId()
                            + " nije pronađena."
            );
        }

        return exerciseRepository
                .findById(sessionExercise.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ažuriranu vežbu nije moguće učitati."
                ));
    }

    /**
     * Briše odrađenu vežbu.
     */
    public void deleteSessionExercise(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        if (!exerciseRepository.deleteById(id)) {
            throw new EntityNotFoundException(
                    "Odrađena vežba sa ID-em " + id
                            + " nije pronađena."
            );
        }
    }

    /**
     * Računa ukupan volumen odrađene vežbe.
     *
     * Volumen = serije × ponavljanja × težina.
     */
    public double calculateVolume(
            SessionExercise sessionExercise
    ) throws ValidationException {

        validateSessionExercise(sessionExercise);

        return sessionExercise.getCompletedSets()
                * sessionExercise.getCompletedReps()
                * sessionExercise.getWeight();
    }

    /**
     * Računa ukupan volumen svih vežbi treninga.
     */
    public double calculateTotalVolume(
            List<SessionExercise> exercises
    ) throws ValidationException {

        if (exercises == null) {
            throw new ValidationException(
                    "Lista odrađenih vežbi nije prosleđena."
            );
        }

        double totalVolume = 0;

        for (SessionExercise exercise : exercises) {
            totalVolume += calculateVolume(exercise);
        }

        return totalVolume;
    }

    private void validateSessionExercise(
            SessionExercise sessionExercise
    ) throws ValidationException {

        if (sessionExercise == null) {
            throw new ValidationException(
                    "Podaci o odrađenoj vežbi nisu prosleđeni."
            );
        }

        if (sessionExercise.getSessionId() <= 0) {
            throw new ValidationException(
                    "Trening nije ispravan."
            );
        }

        if (sessionExercise.getExerciseId() <= 0) {
            throw new ValidationException(
                    "Vežba nije ispravna."
            );
        }

        if (sessionExercise.getCompletedSets() <= 0) {
            throw new ValidationException(
                    "Broj serija mora biti veći od nule."
            );
        }

        if (sessionExercise.getCompletedSets() > 100) {
            throw new ValidationException(
                    "Broj serija ne može biti veći od 100."
            );
        }

        if (sessionExercise.getCompletedReps() <= 0) {
            throw new ValidationException(
                    "Broj ponavljanja mora biti veći od nule."
            );
        }

        if (sessionExercise.getCompletedReps() > 1000) {
            throw new ValidationException(
                    "Broj ponavljanja ne može biti veći od 1000."
            );
        }

        if (sessionExercise.getWeight() < 0) {
            throw new ValidationException(
                    "Težina ne može biti negativna."
            );
        }

        if (sessionExercise.getWeight() > 1000) {
            throw new ValidationException(
                    "Težina ne može biti veća od 1000 kg."
            );
        }
    }

    private void validateSessionId(int sessionId)
            throws ValidationException {

        if (sessionId <= 0) {
            throw new ValidationException(
                    "ID treninga mora biti pozitivan broj."
            );
        }
    }

    private void validateId(int id)
            throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(
                    "ID odrađene vežbe mora biti pozitivan broj."
            );
        }
    }
}