package com.example.fitnesstrackingapp.network;

import com.example.fitnesstrackingapp.model.Exercise;

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