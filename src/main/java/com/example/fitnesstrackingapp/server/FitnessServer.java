package com.example.fitnesstrackingapp.server;


import com.example.fitnesstrackingapp.network.Request;
import com.example.fitnesstrackingapp.network.RequestType;
import com.example.fitnesstrackingapp.network.Response;
import com.example.fitnesstrackingapp.util.DatabaseManager;
import com.example.fitnesstrackingapp.service.ExerciseService;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.exception.EntityNotFoundException;
import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.service.WorkoutPlanService;
import com.example.fitnesstrackingapp.model.PlanExercise;
import com.example.fitnesstrackingapp.service.PlanExerciseService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

//serverski deo fitness aplikacije.

public class FitnessServer {

    // Mrežni port na kojem server prihvata klijente.
    private static final int PORT = 5555;
    private static final ExerciseService EXERCISE_SERVICE =
            new ExerciseService();
    private static final WorkoutPlanService PLAN_SERVICE =
            new WorkoutPlanService();
    private static final PlanExerciseService PLAN_EXERCISE_SERVICE =
            new PlanExerciseService();


    /**
     * Pokreće bazu i mrezni server.
     *
     * @param args argumenti komandne linije
     */
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
            startServer();
        } catch (SQLException | IOException exception) {
            System.err.println(
                    "Pokretanje servera nije uspelo: "
                            + exception.getMessage()
            );
            exception.printStackTrace();
        }
    }

    /**
     * Pokreće server i ceka klijentske konekcije.
     *
     * @throws IOException ako mrezna komunikacija ne uspe
     */
    private static void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(
                    "Fitness server je pokrenut na portu " + PORT + "."
            );
            System.out.println("Server ceka klijenta...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        }
    }

    /**
     * Prima jedan zahtev i vraca jedan odgovor.
     *
     * @param clientSocket konekcija sa klijentom
     */
    private static void handleClient(Socket clientSocket) {
        System.out.println(
                "Klijent je povezan: "
                        + clientSocket.getRemoteSocketAddress()
        );

        try (
                Socket socket = clientSocket;
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

            Object receivedObject = input.readObject();

            Response<?> response;

            if (receivedObject instanceof Request<?> request) {
                System.out.println(
                        "Primljen zahtev: " + request.getType()
                );

                response = handleRequest(request);
            } else {
                response = Response.failure(
                        "Server nije primio ispravan zahtev."
                );
            }

            output.writeObject(response);
            output.flush();

        } catch (IOException | ClassNotFoundException exception) {
            System.err.println(
                    "Greska u komunikaciji sa klijentom: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Obradjuje zahtev prema njegovom tipu.
     *
     * @param request zahtev klijenta
     * @return odgovor servera
     */
    private static Response<?> handleRequest(Request<?> request) {
        RequestType type = request.getType();

        try {
            return switch (type) {
                case PING -> Response.success(
                        "PONG - server radi."
                );

                case GET_ALL_EXERCISES -> Response.success(
                        "Vežbe su uspešno učitane.",
                        EXERCISE_SERVICE.getAllExercises()
                );
                case CREATE_EXERCISE ->
                        handleCreateExercise(request);

                case UPDATE_EXERCISE ->
                        handleUpdateExercise(request);

                case DELETE_EXERCISE ->
                        handleDeleteExercise(request);

                case GET_ALL_PLANS -> Response.success(
                        "Planovi su uspesno ucitani",
                        PLAN_SERVICE.getPlansForDefaultUser()
                );

                case CREATE_PLAN -> handleCreatePlan(request);

                case UPDATE_PLAN -> handleUpdatePlan(request);

                case DELETE_PLAN -> handleDeletePlan(request);

                case GET_PLAN_EXERCISES -> handleGetPlanExercises(request);

                case ADD_EXERCISE_TO_PLAN -> handleAddExerciseToPlan(request);

                case UPDATE_PLAN_EXERCISE -> handleUpdatePlanExercise(request);

                case REMOVE_EXERCISE_FROM_PLAN -> handleRemoveExerciseFromPlan(request);



                default -> Response.failure(
                        "Zahtev još nije implementiran: " + type
                );
            };

        } catch (ValidationException | EntityNotFoundException exception
        ){
            return Response.failure(exception.getMessage());
        }
        catch (SQLException exception) {
            System.err.println(
                    "Greška pri radu sa bazom: "
                            + exception.getMessage()
            );
            exception.printStackTrace();

            return Response.failure(
                    "Server nije uspeo da pristupi bazi."
            );
        }
    }


        private static Response<?> handleCreateExercise(
                Request<?> request
                ) throws ValidationException, SQLException{
            if(!(request.getData() instanceof Exercise exercise)){
                return Response.failure(
                        "Zahtev ne sadrzi ispravne podatke o vezbi."
                );
            }
            Exercise savedExercise =
                    EXERCISE_SERVICE.createExercise(exercise);
            return Response.success("" +
                    "Vezba je uspesno dodata.",
                    savedExercise);
        }

        private static Response<?> handleUpdateExercise(
                Request<?> request
        )throws ValidationException, EntityNotFoundException, SQLException{
        if(!(request.getData() instanceof  Exercise exercise)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravne podatke o vezbi."
            );
        }
        Exercise updatedExercise = EXERCISE_SERVICE.updateExercise(exercise);
        return Response.success("" +
                "Vezba je uspesno izmenjena.", updatedExercise);
        }
        private static Response<?> handleDeleteExercise(
                Request<?> request)
                throws  ValidationException, EntityNotFoundException, SQLException {

            if (!(request.getData() instanceof Integer exerciseID)) {
                return Response.failure("Zahtev ne sadrzi ispravan ID");
            }
            EXERCISE_SERVICE.deleteExercise(exerciseID);
            return Response.success(
                    "Vezba je uspesno obirsana.");

        }

        private static Response<?> handleCreatePlan(
                Request<?> request
        ) throws ValidationException, SQLException{
        if(!(request.getData()instanceof WorkoutPlan plan)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravne podatke o planu."
            );
        }WorkoutPlan savedPlan = PLAN_SERVICE.createPlan(plan);

        return Response.success(
                "Plan je uspesno dodat",
                savedPlan
        );
        }

    private static Response<?> handleUpdatePlan(
            Request<?> request
    ) throws ValidationException, SQLException, EntityNotFoundException{
        if(!(request.getData()instanceof WorkoutPlan plan)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravne podatke o planu."
            );
        }WorkoutPlan updatedPlan = PLAN_SERVICE.updatePlan(plan);

        return Response.success(
                "Plan je uspesno izmenjen",
                updatedPlan
        );
    }

    private static Response<?> handleDeletePlan(
            Request<?> request
    ) throws ValidationException, SQLException, EntityNotFoundException{
        if(!(request.getData()instanceof Integer planId)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravan ID plana."
            );
        } PLAN_SERVICE.deletePlan(planId);

        return Response.success(
                "Plan je uspesno obrisan"

        );
    }
    private static Response<?> handleGetPlanExercises(
            Request<?> request
    )throws ValidationException, EntityNotFoundException, SQLException{
        if(!(request.getData() instanceof  Integer planId)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravan ID plana. "
            );
        }
        return Response.success(
                "Stavke plana uspesno ucitane. ",
                PLAN_EXERCISE_SERVICE.getItemsForPlan(planId)
        );
    }

    private static Response<?> handleAddExerciseToPlan(
            Request<?> request
    )throws ValidationException, EntityNotFoundException, SQLException{
        if(!(request.getData() instanceof  PlanExercise item)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravnu stavku plana. "
            );
        }
        PlanExercise savedItem =PLAN_EXERCISE_SERVICE.addExerciseToPlan(item);
        return Response.success(
                "Vezba uspesno dodata u plan. ",
                savedItem
        );
    }

    private static Response<?> handleUpdatePlanExercise(
            Request<?> request
    )throws ValidationException, EntityNotFoundException, SQLException{
        if(!(request.getData() instanceof  PlanExercise item)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravnu stavku plana. "
            );
        }
        PlanExercise updatedItem = PLAN_EXERCISE_SERVICE.updatePlanExercise(item);
        return Response.success(
                "Stavka plana uspesno izmenjena. ",
                updatedItem
        );
    }

    private static Response<?> handleRemoveExerciseFromPlan(
            Request<?> request
    )throws ValidationException, EntityNotFoundException, SQLException{
        if(!(request.getData() instanceof  Integer itemId)){
            return Response.failure(
                    "Zahtev ne sadrzi ispravan ID stavke plana. "
            );
        }
        PLAN_EXERCISE_SERVICE.removeExerciseFromPlan(itemId);

        return Response.success(
                "Vezba je uspesno uklonjena iz plana "

        );
    }


}