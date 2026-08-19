package com.example.fitnesstrackingapp.service;

import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.repository.WorkoutSessionRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Sadrži poslovnu logiku i validaciju održanih treninga.
 */
public class WorkoutSessionService {

    public static final int DEFAULT_USER_ID = 1;

    private final WorkoutSessionRepository sessionRepository;

    public WorkoutSessionService() {
        this(new WorkoutSessionRepository());
    }

    public WorkoutSessionService(
            WorkoutSessionRepository sessionRepository
    ) {
        this.sessionRepository = Objects.requireNonNull(
                sessionRepository,
                "WorkoutSessionRepository ne sme biti null."
        );
    }

    /**
     * Validira i čuva novi održani trening.
     */
    public WorkoutSession createSession(
            WorkoutSession session
    ) throws ValidationException, SQLException {

        validateSession(session);
        normalizeSession(session);

        return sessionRepository.create(session);
    }

    /**
     * Vraća treninge podrazumevanog korisnika.
     */
    public List<WorkoutSession> getSessionsForDefaultUser()
            throws SQLException {

        return sessionRepository.findByUserId(
                DEFAULT_USER_ID
        );
    }

    /**
     * Pronalazi održani trening prema ID-u.
     */
    public WorkoutSession getSessionById(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Trening sa ID-em " + id
                                + " nije pronađen."
                ));
    }

    /**
     * Validira i ažurira održani trening.
     */
    public WorkoutSession updateSession(
            WorkoutSession session
    ) throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateSession(session);
        validateId(session.getId());
        normalizeSession(session);

        if (!sessionRepository.update(session)) {
            throw new EntityNotFoundException(
                    "Trening sa ID-em " + session.getId()
                            + " nije pronađen."
            );
        }

        return sessionRepository.findById(session.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ažurirani trening nije pronađen."
                ));
    }

    /**
     * Briše održani trening.
     */
    public void deleteSession(int id)
            throws ValidationException,
            EntityNotFoundException,
            SQLException {

        validateId(id);

        if (!sessionRepository.deleteById(id)) {
            throw new EntityNotFoundException(
                    "Trening sa ID-em " + id
                            + " nije pronađen."
            );
        }
    }

    /**
     * Proverava podatke održanog treninga.
     */
    private void validateSession(WorkoutSession session)
            throws ValidationException {

        if (session == null) {
            throw new ValidationException(
                    "Podaci o treningu nisu prosleđeni."
            );
        }

        if (session.getUserId() <= 0) {
            throw new ValidationException(
                    "Korisnik treninga nije ispravan."
            );
        }

        if (session.getPlanId() != null
                && session.getPlanId() <= 0) {
            throw new ValidationException(
                    "Izabrani plan nije ispravan."
            );
        }

        if (session.getWorkoutDate() == null) {
            throw new ValidationException(
                    "Datum treninga je obavezan."
            );
        }

        if (session.getWorkoutDate().isAfter(
                LocalDate.now()
        )) {
            throw new ValidationException(
                    "Datum treninga ne može biti u budućnosti."
            );
        }

        if (session.getDurationMinutes() <= 0) {
            throw new ValidationException(
                    "Trajanje treninga mora biti veće od nule."
            );
        }

        if (session.getDurationMinutes() > 1440) {
            throw new ValidationException(
                    "Trening ne može trajati duže od 1440 minuta."
            );
        }

        if (session.getNotes() != null
                && session.getNotes().trim().length() > 500) {
            throw new ValidationException(
                    "Napomena može imati najviše 500 znakova."
            );
        }
    }

    /**
     * Uklanja nepotrebne razmake iz napomene.
     */
    private void normalizeSession(
            WorkoutSession session
    ) {
        if (session.getNotes() == null) {
            session.setNotes("");
        } else {
            session.setNotes(
                    session.getNotes()
                            .trim()
                            .replaceAll("\\s+", " ")
            );
        }
    }

    private void validateId(int id)
            throws ValidationException {

        if (id <= 0) {
            throw new ValidationException(
                    "ID treninga mora biti pozitivan broj."
            );
        }
    }
}