package com.example.fitnesstrackingapp.server;

import com.example.fitnesstrackingapp.util.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

//serverski deo fitness aplikacije.

public class FitnessServer {

    // Mrezni port na kojem server prihvata klijente.
    private static final int PORT = 5555;

    // Pokrece bazu i mrezni server.

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
     * Pokrece server i ceka klijentske konekcije.
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


     // Prima jednu poruku od klijenta i vraća odgovor.


    private static void handleClient(Socket clientSocket) {
        System.out.println(
                "Klijent je povezan: "
                        + clientSocket.getRemoteSocketAddress()
        );

        try (
                Socket socket = clientSocket;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            String message = reader.readLine();
            System.out.println(
                    "Primljena poruka: " + message
            );

            writer.println(
                    "Server je primio poruku: " + message
            );
        } catch (IOException exception) {
            System.err.println(
                    "Greska u komunikaciji sa klijentom: "
                            + exception.getMessage()
            );
        }
    }
}