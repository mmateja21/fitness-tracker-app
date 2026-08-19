package com.example.fitnesstrackingapp.network;

import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.model.PlanExercise;
import com.example.fitnesstrackingapp.model.SessionExercise;
import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.service.PlanExerciseService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Omogućava JavaFX klijentu komunikaciju sa serverom.

public class FitnessClient {


    private static final String SERVER_HOST = "localhost";


    private static final int SERVER_PORT = 5555;

    /**
     * Proverava dostupnost servera.
     *
     * @return odgovor servera
     * @throws IOException ako mrežna komunikacija ne uspe
     * @throws ClassNotFoundException ako odgovor ne može da se učita
     */
    public Response<?> ping()
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(RequestType.PING)
        );
    }

    /**
     * Zahteva sve vežbe sa servera.
     *
     * @return odgovor koji sadrži listu vežbi
     * @throws IOException ako mrežna komunikacija ne uspe
     * @throws ClassNotFoundException ako odgovor ne može da se učita
     */
    public Response<?> getAllExercises()
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.GET_ALL_EXERCISES
                )
        );
    }

    //Šalje novu vežbu serveru.


    public Response<?> createExercise(Exercise exercise)
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.CREATE_EXERCISE,
                        exercise
                )
        );
    }

    //Šalje izmenjenu vežbu serveru.

    public Response<?> updateExercise(Exercise exercise)
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.UPDATE_EXERCISE,
                        exercise
                )
        );
    }

    //Zahteva brisanje vežbe.

    public Response<?> deleteExercise(int exerciseId)
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.DELETE_EXERCISE,
                        exerciseId
                )
        );
    }

    public Response<?>getAllPlans()
        throws IOException, ClassNotFoundException{
        return sendRequest(
                new Request<>(
                        RequestType.GET_ALL_PLANS
                )
        );
    }

    public Response<?> createPlan(WorkoutPlan plan)
        throws IOException, ClassNotFoundException{

        return sendRequest(
                new Request<>(
                        RequestType.CREATE_PLAN,
                        plan
                )
        );
    }

    public Response<?>updatePlan(WorkoutPlan plan)
        throws IOException, ClassNotFoundException{
        return sendRequest(
                new Request<>(
                        RequestType.UPDATE_PLAN,
                        plan
                )
        );
    }

    public Response<?> deletePlan (int planId)
        throws IOException, ClassNotFoundException{

        return sendRequest(
                new Request<>(
                        RequestType.DELETE_PLAN,
                        planId
                )
        );
    }

    public Response<?> getPlanExercises(int planId)
        throws IOException, ClassNotFoundException{
        return sendRequest(
                new Request<>(RequestType.GET_PLAN_EXERCISES,
                        planId)
        );
    }

    public Response<?> addExerciseToPlan (PlanExercise item)
        throws IOException, ClassNotFoundException{
        return sendRequest(
                new Request<>(
                        RequestType.ADD_EXERCISE_TO_PLAN,
                        item
                )
        );
    }

    public Response<?> updatePlanExercise (PlanExercise item)
        throws IOException,ClassNotFoundException{
        return sendRequest(
                new Request<>(
                        RequestType.UPDATE_PLAN_EXERCISE,
                        item
                )
        );
    }


    public Response<?> removeExerciseFromPlan(int itemId)
        throws IOException,ClassNotFoundException{

        return sendRequest(new Request<>(
                RequestType.REMOVE_EXERCISE_FROM_PLAN,
                itemId
        ));
    }
    public Response<?> getAllSessions()
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.GET_ALL_SESSIONS
                )
        );
    }

    public Response<?> createSession(
            WorkoutSession session
    ) throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.CREATE_SESSION,
                        session
                )
        );
    }

    public Response<?> updateSession(
            WorkoutSession session
    ) throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.UPDATE_SESSION,
                        session
                )
        );
    }

    public Response<?> deleteSession(int sessionId)
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.DELETE_SESSION,
                        sessionId
                )
        );
    }

    public Response<?> getSessionExercises(int sessionId)
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.GET_SESSION_EXERCISES,
                        sessionId
                )
        );
    }

    public Response<?> addSessionExercise(
            SessionExercise sessionExercise
    ) throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.ADD_SESSION_EXERCISE,
                        sessionExercise
                )
        );
    }

    public Response<?> updateSessionExercise(
            SessionExercise sessionExercise
    ) throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.UPDATE_SESSION_EXERCISE,
                        sessionExercise
                )
        );
    }

    public Response<?> removeSessionExercise(
            int sessionExerciseId
    ) throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.REMOVE_SESSION_EXERCISE,
                        sessionExerciseId
                )
        );
    }

    /**
     * Zahteva zbirne podatke o treninzima.
     */
    public Response<?> getWorkoutSummary()
            throws IOException, ClassNotFoundException {

        return sendRequest(
                new Request<>(
                        RequestType.GET_WORKOUT_SUMMARY
                )
        );
    }



    //Šalje zahtev serveru i vraća primljeni odgovor.

    public Response<?> sendRequest(Request<?> request)
            throws IOException, ClassNotFoundException {

        try (
                Socket socket =
                        new Socket(SERVER_HOST, SERVER_PORT);
                ObjectOutputStream output =
                        new ObjectOutputStream(
                                socket.getOutputStream()
                        );
                ObjectInputStream input =
                        new ObjectInputStream(
                                socket.getInputStream()
                        )
        ) {
            output.flush();
            output.writeObject(request);
            output.flush();

            Object receivedObject = input.readObject();

            if (receivedObject instanceof Response<?> response) {
                return response;
            }

            throw new IOException(
                    "Server nije vratio ispravan odgovor."
            );
        }
    }
}