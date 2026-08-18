package com.example.fitnesstrackingapp.network;
import com.example.fitnesstrackingapp.model.MuscleGroup;
import com.example.fitnesstrackingapp.model.Exercise;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//slanje zahteva serveru
public class FitnessClient {


    private static final String SERVER_HOST = "localhost";


    private static final int SERVER_PORT = 5555;


      //Salje zahtev serveru i vraca primljeni odgovor


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




    //Šalje probnu vežbu serveru.

    public static void main(String[] args) {
        FitnessClient client = new FitnessClient();

        Exercise exercise = new Exercise(
                "Bench Press",
                MuscleGroup.CHEST,
                "Sipka i ravna klupa",
                "Potisak sipke sa ravne klupe."
        );

        Request<Exercise> request = new Request<>(
                RequestType.CREATE_EXERCISE,
                exercise
        );

        try {
            Response<?> response =
                    client.sendRequest(request);

            System.out.println(
                    "Uspesan odgovor: "
                            + response.isSuccessful()
            );

            System.out.println(
                    "Poruka servera: "
                            + response.getMessage()
            );

            if (response.getData() instanceof Exercise savedExercise) {
                System.out.println(
                        "Dodeljeni ID: "
                                + savedExercise.getId()
                );

                System.out.println(
                        "Sacuvana vezba: "
                                + savedExercise.getName()
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            System.err.println(
                    "Komunikacija sa serverom nije uspela: "
                            + exception.getMessage()
            );
        }
    }}