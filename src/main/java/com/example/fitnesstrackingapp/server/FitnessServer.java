package com.example.fitnesstrackingapp.server;

import com.example.fitnesstrackingapp.network.Request;
import com.example.fitnesstrackingapp.network.RequestType;
import com.example.fitnesstrackingapp.network.Response;
import com.example.fitnesstrackingapp.util.DatabaseManager;
import com.example.fitnesstrackingapp.service.ExerciseService;
import com.example.fitnesstrackingapp.exception.ValidationException;
import com.example.fitnesstrackingapp.model.Exercise;

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

                default -> Response.failure(
                        "Zahtev još nije implementiran: " + type
                );
            };

        } catch (ValidationException exception){
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

}